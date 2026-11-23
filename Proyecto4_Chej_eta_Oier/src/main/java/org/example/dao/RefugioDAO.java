package org.example.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.example.config.Conexion;
import org.example.modelo.Refugio;
import org.example.util.Fechas;

import static com.mongodb.client.model.Filters.eq;

/** La colección "refugio" tiene un único documento, siempre con _id = "refugio". */
public class RefugioDAO {

    private static final String COLECCION = "refugio";
    private static final String ID = "refugio";

    private MongoCollection<Document> coleccion() {
        return Conexion.bd().getCollection(COLECCION);
    }

    public Refugio obtener() {
        Document documento = coleccion().find(eq("_id", ID)).first();
        return documento == null ? null : aRefugio(documento);
    }

    /** upsert: crea el documento si no existe o lo reemplaza si ya existe. */
    public void guardar(Refugio refugio) {
        coleccion().replaceOne(eq("_id", ID), aDocumento(refugio), new ReplaceOptions().upsert(true));
    }

    public static Document aDocumento(Refugio refugio) {
        return new Document("_id", ID)
                .append("nombre", refugio.getNombre())
                .append("cif", refugio.getCif())
                .append("direccion", refugio.getDireccion())
                .append("telefono", refugio.getTelefono())
                .append("email", refugio.getEmail())
                .append("fechaApertura", Fechas.aDate(refugio.getFechaApertura()));
    }

    public static Refugio aRefugio(Document documento) {
        return new Refugio(
                documento.getString("nombre"),
                documento.getString("cif"),
                documento.getString("direccion"),
                documento.getString("telefono"),
                documento.getString("email"),
                Fechas.aLocalDate(documento.getDate("fechaApertura")));
    }
}