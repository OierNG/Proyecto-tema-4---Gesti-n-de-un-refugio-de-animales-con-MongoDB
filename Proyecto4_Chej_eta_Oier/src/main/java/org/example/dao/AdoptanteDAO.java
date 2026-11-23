package org.example.dao;

import org.bson.Document;
import org.example.modelo.Adoptante;
import org.example.modelo.Preferencias;
import org.example.util.Fechas;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;

public class AdoptanteDAO extends BaseDAO<Adoptante> {

    public AdoptanteDAO() {
        super("adoptantes");
    }

    @Override
    public void insertar(Adoptante adoptante) {
        adoptante.setId(siguienteId());
        super.insertar(adoptante);
    }

    /** Incluye los marcados como eliminados, porque el índice único de dni también los cuenta. */
    public boolean existeDni(String dni, int idExcluido) {
        return coleccion().countDocuments(and(eq("dni", dni), ne("_id", idExcluido))) > 0;
    }

    public boolean tieneAdopciones(int idAdoptante) {
        return estaReferenciado("adopciones", "adoptanteId", idAdoptante);
    }

    @Override
    protected Document aDocumento(Adoptante adoptante) {
        Preferencias p = adoptante.getPreferencias();
        Document preferencias = new Document("especie", p.getEspecie())
                .append("sexo", p.getSexo())
                .append("edadMaxima", p.getEdadMaxima());

        return new Document("_id", adoptante.getId())
                .append("dni", adoptante.getDni())
                .append("nombre", adoptante.getNombre())
                .append("apellidos", adoptante.getApellidos())
                .append("telefono", adoptante.getTelefono())
                .append("direccion", adoptante.getDireccion())
                .append("email", adoptante.getEmail())
                .append("fechaRegistro", Fechas.aDate(adoptante.getFechaRegistro()))
                .append("preferencias", preferencias)
                .append("eliminado", adoptante.isEliminado());
    }

    @Override
    protected Adoptante aObjeto(Document documento) {
        Adoptante adoptante = new Adoptante();
        adoptante.setId(documento.getInteger("_id"));
        adoptante.setDni(documento.getString("dni"));
        adoptante.setNombre(documento.getString("nombre"));
        adoptante.setApellidos(documento.getString("apellidos"));
        adoptante.setTelefono(documento.getString("telefono"));
        adoptante.setDireccion(documento.getString("direccion"));
        adoptante.setEmail(documento.getString("email"));
        adoptante.setFechaRegistro(Fechas.aLocalDate(documento.getDate("fechaRegistro")));
        adoptante.setEliminado(documento.getBoolean("eliminado", false));

        Document p = documento.get("preferencias", new Document());
        adoptante.setPreferencias(new Preferencias(
                p.getString("especie"),
                p.getString("sexo"),
                p.getInteger("edadMaxima", 0)));
        return adoptante;
    }
}
