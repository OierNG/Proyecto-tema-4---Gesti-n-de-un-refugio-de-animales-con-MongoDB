package org.example.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.example.config.Conexion;
import org.example.modelo.Refugio;
import org.example.util.Fechas;

import static com.mongodb.client.model.Filters.eq;

/**
 * Acceso a la colección "refugio".
 *
 * Sólo hay un documento de configuración con los datos del refugio, por eso
 * se identifica siempre con _id = "refugio".
 */
public class RefugioDAO {

    private static final String COLECCION = "refugio";
    private static final String ID = "refugio";

    private MongoCollection<Document> coleccion() {
        return Conexion.bd().getCollection(COLECCION);
    }

    /** Devuelve los datos del refugio o null si todavía no existen. */
    public Refugio obtener() {
        Document documento = coleccion().find(eq("_id", ID)).first();
        return documento == null ? null : aRefugio(documento);
    }

    /** Guarda (crea o reemplaza) los datos del refugio. */
    public void guardar(Refugio refugio) {
        coleccion().replaceOne(eq("_id", ID), aDocumento(refugio), new ReplaceOptions().upsert(true));
    }

    /** Refugio -> Document (mapeo manual, sin ODM). */
    public static Document aDocumento(Refugio refugio) {
        return new Document("_id", ID)
                .append("nombre", refugio.getNombre())
                .append("cif", refugio.getCif())
                .append("direccion", refugio.getDireccion())
                .append("telefono", refugio.getTelefono())
                .append("email", refugio.getEmail())
                .append("fechaApertura", Fechas.aDate(refugio.getFechaApertura()));
    }

    /** Document -> Refugio. */
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