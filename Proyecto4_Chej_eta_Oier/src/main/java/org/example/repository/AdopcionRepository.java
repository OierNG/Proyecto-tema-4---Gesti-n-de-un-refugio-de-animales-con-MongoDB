package org.example.repository;

import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.example.model.Adopcion;
import org.example.model.EstadoAdopcion;
import org.example.model.Seguimiento;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class AdopcionRepository extends RepositorioBase<Adopcion> {

    public AdopcionRepository() {
        super("adopciones");
    }

    @Override
    public void guardar(Adopcion adopcion) {
        adopcion.setId(siguienteId());
        super.guardar(adopcion);
    }

    public List<Adopcion> buscarPorAdoptante(int idAdoptante) {
        return buscar(and(eq("adoptanteId", idAdoptante), NO_ELIMINADO));
    }

    // Cuenta también las marcadas como eliminadas: el documento sigue existiendo y apunta al animal/adoptante
    public long contarPorAnimal(int idAnimal) {
        return contar(eq("animalId", idAnimal));
    }

    public long contarPorAdoptante(int idAdoptante) {
        return contar(eq("adoptanteId", idAdoptante));
    }

    public void anadirSeguimiento(int idAdopcion, Seguimiento seguimiento) {
        ejecutar(coleccion -> coleccion.updateOne(eq("_id", idAdopcion),
                Updates.push("seguimiento", aDocumento(seguimiento))));
    }

    @Override
    protected Document aDocumento(Adopcion adopcion) {
        return new Document("_id", adopcion.getId())
                .append("animalId", adopcion.getAnimalId())
                .append("adoptanteId", adopcion.getAdoptanteId())
                .append("fechaSolicitud", aDate(adopcion.getFechaSolicitud()))
                .append("fechaResolucion", aDate(adopcion.getFechaResolucion()))
                .append("estado", adopcion.getEstado().name())
                .append("observaciones", adopcion.getObservaciones())
                .append("seguimiento", adopcion.getSeguimiento().stream().map(this::aDocumento).toList())
                .append("eliminado", adopcion.isEliminado());
    }

    @Override
    protected Adopcion aEntidad(Document documento) {
        Adopcion adopcion = new Adopcion(
                documento.getInteger("animalId", 0),
                documento.getInteger("adoptanteId", 0),
                aLocalDate(documento.getDate("fechaSolicitud")),
                documento.getString("observaciones"));
        adopcion.setId(documento.getInteger("_id"));
        adopcion.setFechaResolucion(aLocalDate(documento.getDate("fechaResolucion")));
        adopcion.setEstado(EstadoAdopcion.valueOf(documento.getString("estado")));
        adopcion.setEliminado(documento.getBoolean("eliminado", false));
        for (Document s : documento.getList("seguimiento", Document.class, new ArrayList<>())) {
            adopcion.getSeguimiento().add(new Seguimiento(aLocalDate(s.getDate("fecha")), s.getString("descripcion")));
        }
        return adopcion;
    }

    private Document aDocumento(Seguimiento seguimiento) {
        return new Document("fecha", aDate(seguimiento.getFecha()))
                .append("descripcion", seguimiento.getDescripcion());
    }
}
