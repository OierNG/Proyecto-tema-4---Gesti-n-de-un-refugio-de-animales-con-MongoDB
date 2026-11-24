package org.example.dao;

import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.example.modelo.Adopcion;
import org.example.modelo.EstadoAdopcion;
import org.example.modelo.Seguimiento;
import org.example.util.Fechas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class AdopcionDAO extends BaseDAO<Adopcion> {

    public AdopcionDAO() {
        super("adopciones");
    }

    @Override
    public void insertar(Adopcion adopcion) {
        adopcion.setId(siguienteId());
        super.insertar(adopcion);
    }

    public List<Adopcion> buscarPorAdoptante(int idAdoptante) {
        return buscar(and(eq("adoptanteId", idAdoptante), NO_ELIMINADO));
    }

    /** Aprueba o rechaza: cambia el estado y guarda la fecha de resolución. */
    public void resolver(int idAdopcion, EstadoAdopcion estado, LocalDate fecha) {
        coleccion().updateOne(eq("_id", idAdopcion), Updates.combine(
                Updates.set("estado", estado.name()),
                Updates.set("fechaResolucion", Fechas.aDate(fecha))));
    }

    public void anadirSeguimiento(int idAdopcion, Seguimiento seguimiento) {
        coleccion().updateOne(eq("_id", idAdopcion), Updates.push("seguimiento", aDocumento(seguimiento)));
    }

    @Override
    protected Document aDocumento(Adopcion adopcion) {
        List<Document> seguimiento = new ArrayList<>();
        for (Seguimiento s : adopcion.getSeguimiento()) {
            seguimiento.add(aDocumento(s));
        }
        return new Document("_id", adopcion.getId())
                .append("animalId", adopcion.getAnimalId())
                .append("adoptanteId", adopcion.getAdoptanteId())
                .append("fechaSolicitud", Fechas.aDate(adopcion.getFechaSolicitud()))
                .append("fechaResolucion", Fechas.aDate(adopcion.getFechaResolucion()))
                .append("estado", adopcion.getEstado().name())
                .append("observaciones", adopcion.getObservaciones())
                .append("seguimiento", seguimiento)
                .append("eliminado", adopcion.isEliminado());
    }

    @Override
    protected Adopcion aObjeto(Document documento) {
        Adopcion adopcion = new Adopcion();
        adopcion.setId(documento.getInteger("_id"));
        adopcion.setAnimalId(documento.getInteger("animalId", 0));
        adopcion.setAdoptanteId(documento.getInteger("adoptanteId", 0));
        adopcion.setFechaSolicitud(Fechas.aLocalDate(documento.getDate("fechaSolicitud")));
        adopcion.setFechaResolucion(Fechas.aLocalDate(documento.getDate("fechaResolucion")));
        String estado = documento.getString("estado");
        adopcion.setEstado(estado == null ? EstadoAdopcion.PENDIENTE : EstadoAdopcion.valueOf(estado));
        adopcion.setObservaciones(documento.getString("observaciones"));
        adopcion.setEliminado(documento.getBoolean("eliminado", false));

        for (Document s : documento.getList("seguimiento", Document.class, new ArrayList<>())) {
            adopcion.getSeguimiento().add(new Seguimiento(
                    Fechas.aLocalDate(s.getDate("fecha")),
                    s.getString("descripcion")));
        }
        return adopcion;
    }

    private Document aDocumento(Seguimiento seguimiento) {
        return new Document("fecha", Fechas.aDate(seguimiento.getFecha()))
                .append("descripcion", seguimiento.getDescripcion());
    }
}