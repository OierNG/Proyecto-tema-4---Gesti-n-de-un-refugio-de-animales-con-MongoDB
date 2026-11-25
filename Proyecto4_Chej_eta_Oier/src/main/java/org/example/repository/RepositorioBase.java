package org.example.repository;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.exception.EntidadNoEncontradaException;
import org.example.exception.IntegridadException;
import org.example.exception.RefugioException;
import org.example.util.MongoUtil;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;

// Operaciones CRUD comunes a todas las colecciones. Cada repositorio hijo solo define la conversión objeto <-> Document
public abstract class RepositorioBase<T> {

    protected static final Bson NO_ELIMINADO = ne("eliminado", true);

    private final String nombreColeccion;

    protected RepositorioBase(String nombreColeccion) {
        this.nombreColeccion = nombreColeccion;
    }

    protected abstract Document aDocumento(T entidad);

    protected abstract T aEntidad(Document documento);

    public void guardar(T entidad) {
        ejecutar(coleccion -> coleccion.insertOne(aDocumento(entidad)));
    }

    public void actualizar(T entidad) {
        Document documento = aDocumento(entidad);
        ejecutar(coleccion -> coleccion.replaceOne(eq("_id", documento.get("_id")), documento));
    }

    public void eliminarPorId(int id) {
        ejecutar(coleccion -> {
            if (coleccion.deleteOne(eq("_id", id)).getDeletedCount() == 0) {
                throw new EntidadNoEncontradaException("No existe ningún registro con id " + id + ".");
            }
        });
    }

    // Baja lógica: el documento se conserva con eliminado = true
    public void marcarEliminado(int id) {
        ejecutar(coleccion -> coleccion.updateOne(eq("_id", id), Updates.set("eliminado", true)));
    }

    public Optional<T> buscarPorId(int id) {
        return consultar(coleccion -> Optional.ofNullable(coleccion.find(and(eq("_id", id), NO_ELIMINADO)).first())
                .map(this::aEntidad));
    }

    public List<T> listarTodos() {
        return buscar(NO_ELIMINADO);
    }

    protected List<T> buscar(Bson filtro) {
        return consultar(coleccion -> {
            List<T> lista = new ArrayList<>();
            coleccion.find(filtro).sort(Sorts.ascending("_id")).forEach(documento -> lista.add(aEntidad(documento)));
            return lista;
        });
    }

    protected long contar(Bson filtro) {
        return consultar(coleccion -> coleccion.countDocuments(filtro));
    }

    // Id numérico autoincremental guardado en la colección "contadores"
    protected int siguienteId() {
        try {
            return MongoUtil.getBaseDatos().getCollection("contadores").findOneAndUpdate(
                    eq("_id", nombreColeccion),
                    Updates.inc("valor", 1),
                    new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER))
                    .getInteger("valor");
        } catch (MongoException e) {
            throw traducirError(e);
        }
    }

    protected <R> R consultar(Function<MongoCollection<Document>, R> consulta) {
        try {
            return consulta.apply(MongoUtil.getBaseDatos().getCollection(nombreColeccion));
        } catch (MongoException e) {
            throw traducirError(e);
        }
    }

    protected void ejecutar(Consumer<MongoCollection<Document>> accion) {
        consultar(coleccion -> {
            accion.accept(coleccion);
            return null;
        });
    }

    // Convierte los errores técnicos de MongoDB en excepciones con mensaje para el usuario
    private RefugioException traducirError(MongoException e) {
        if (e instanceof MongoWriteException escritura
                && escritura.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
            return new IntegridadException("La operación no es posible: ya existe un registro con ese valor único.");
        }
        return new RefugioException("Error al acceder a la base de datos. Comprueba que MongoDB está arrancado.", e);
    }

    // MongoDB guarda fechas (Date), en Java usamos LocalDate: se guardan a medianoche UTC
    protected static Date aDate(LocalDate fecha) {
        return fecha == null ? null : Date.from(fecha.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    protected static LocalDate aLocalDate(Date fecha) {
        return fecha == null ? null : fecha.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
    }
}
