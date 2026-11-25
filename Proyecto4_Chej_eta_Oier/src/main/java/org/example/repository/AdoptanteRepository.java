package org.example.repository;

import org.bson.Document;
import org.example.model.Adoptante;
import org.example.model.Preferencias;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;

public class AdoptanteRepository extends RepositorioBase<Adoptante> {

    public AdoptanteRepository() {
        super("adoptantes");
    }

    @Override
    public void guardar(Adoptante adoptante) {
        adoptante.setId(siguienteId());
        super.guardar(adoptante);
    }

    // Se excluye el propio adoptante para poder modificarlo sin cambiar el DNI
    public boolean existeDni(String dni, int idExcluido) {
        return contar(and(eq("dni", dni), ne("_id", idExcluido))) > 0;
    }

    @Override
    protected Document aDocumento(Adoptante adoptante) {
        Preferencias p = adoptante.getPreferencias();
        return new Document("_id", adoptante.getId())
                .append("dni", adoptante.getDni())
                .append("nombre", adoptante.getNombre())
                .append("apellidos", adoptante.getApellidos())
                .append("telefono", adoptante.getTelefono())
                .append("direccion", adoptante.getDireccion())
                .append("email", adoptante.getEmail())
                .append("fechaRegistro", aDate(adoptante.getFechaRegistro()))
                .append("preferencias", new Document("especie", p.getEspecie())
                        .append("sexo", p.getSexo())
                        .append("edadMaxima", p.getEdadMaxima()))
                .append("eliminado", adoptante.isEliminado());
    }

    @Override
    protected Adoptante aEntidad(Document documento) {
        Document p = documento.get("preferencias", new Document());
        Adoptante adoptante = new Adoptante(
                documento.getString("dni"),
                documento.getString("nombre"),
                documento.getString("apellidos"),
                documento.getString("telefono"),
                documento.getString("direccion"),
                documento.getString("email"),
                aLocalDate(documento.getDate("fechaRegistro")),
                new Preferencias(p.getString("especie"), p.getString("sexo"), p.getInteger("edadMaxima", 0)));
        adoptante.setId(documento.getInteger("_id"));
        adoptante.setEliminado(documento.getBoolean("eliminado", false));
        return adoptante;
    }
}
