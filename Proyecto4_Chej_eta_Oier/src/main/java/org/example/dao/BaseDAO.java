package org.example.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.config.Conexion;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;

/**
 * Operaciones CRUD comunes a todas las colecciones.
 * Cada DAO hijo solo define cómo se convierte su clase a Document y viceversa.
 */
public abstract class BaseDAO<T> {

    protected static final Bson NO_ELIMINADO = ne("eliminado", true);

    private final String nombreColeccion;

    protected BaseDAO(String nombreColeccion) {
        this.nombreColeccion = nombreColeccion;
    }

    protected abstract Document aDocumento(T objeto);

    protected abstract T aObjeto(Document documento);

    protected MongoCollection<Document> coleccion() {
        return Conexion.bd().getCollection(nombreColeccion);
    }

    public List<T> listar() {
        return buscar(NO_ELIMINADO);
    }

    public List<T> buscar(Bson filtro) {
        List<T> lista = new ArrayList<>();
        for (Document documento : coleccion().find(filtro).sort(Sorts.ascending("_id"))) {
            lista.add(aObjeto(documento));
        }
        return lista;
    }

    public T buscarPorId(Object id) {
        Document documento = coleccion().find(and(eq("_id", id), NO_ELIMINADO)).first();
        return documento == null ? null : aObjeto(documento);
    }

    public void insertar(T objeto) {
        coleccion().insertOne(aDocumento(objeto));
    }

    public void actualizar(T objeto) {
        Document documento = aDocumento(objeto);
        coleccion().replaceOne(eq("_id", documento.get("_id")), documento);
    }

    public void marcarEliminado(Object id) {
        coleccion().updateOne(eq("_id", id), Updates.set("eliminado", true));
    }

    public void eliminar(Object id) {
        coleccion().deleteOne(eq("_id", id));
    }

    /** Id numérico autoincremental guardado en la colección "contadores". */
    protected int siguienteId() {
        Document contador = Conexion.bd().getCollection("contadores").findOneAndUpdate(
                eq("_id", nombreColeccion),
                Updates.inc("valor", 1),
                new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER));
        return contador.getInteger("valor");
    }
}
