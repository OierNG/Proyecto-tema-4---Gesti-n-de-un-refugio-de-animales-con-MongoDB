package org.example.repository;

import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.example.model.Refugio;

import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

// La colección "refugio" tiene un único documento, siempre con _id = "refugio"
public class RefugioRepository extends RepositorioBase<Refugio> {

    private static final String ID = "refugio";

    public RefugioRepository() {
        super("refugio");
    }

    public Optional<Refugio> obtener() {
        return consultar(coleccion -> Optional.ofNullable(coleccion.find(eq("_id", ID)).first()).map(this::aEntidad));
    }

    // upsert: crea el documento si no existe o lo reemplaza si ya existe
    @Override
    public void guardar(Refugio refugio) {
        ejecutar(coleccion -> coleccion.replaceOne(eq("_id", ID), aDocumento(refugio), new ReplaceOptions().upsert(true)));
    }

    @Override
    protected Document aDocumento(Refugio refugio) {
        return new Document("_id", ID)
                .append("nombre", refugio.getNombre())
                .append("cif", refugio.getCif())
                .append("direccion", refugio.getDireccion())
                .append("telefono", refugio.getTelefono())
                .append("email", refugio.getEmail())
                .append("fechaApertura", aDate(refugio.getFechaApertura()));
    }

    @Override
    protected Refugio aEntidad(Document documento) {
        return new Refugio(
                documento.getString("nombre"),
                documento.getString("cif"),
                documento.getString("direccion"),
                documento.getString("telefono"),
                documento.getString("email"),
                aLocalDate(documento.getDate("fechaApertura")));
    }
}
