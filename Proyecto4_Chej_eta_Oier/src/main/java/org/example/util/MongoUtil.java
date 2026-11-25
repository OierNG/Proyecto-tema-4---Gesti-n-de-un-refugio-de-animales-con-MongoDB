package org.example.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

// Gestiona el MongoClient (patrón Singleton): un único cliente compartido por todos los repositorios
public class MongoUtil {

    // Si MongoDB no responde en 3 segundos se avisa en vez de esperar los 30 por defecto
    private static final String URI = "mongodb://localhost:27017/?serverSelectionTimeoutMS=3000";
    public static final String BASE_DATOS = "refugio";

    private static MongoClient cliente;

    private MongoUtil() {
    }

    public static MongoDatabase getBaseDatos() {
        if (cliente == null) {
            cliente = MongoClients.create(URI);
        }
        return cliente.getDatabase(BASE_DATOS);
    }

    // Comprueba la conexión y crea los índices si no existen
    public static void inicializar() {
        MongoDatabase bd = getBaseDatos();
        bd.runCommand(new Document("ping", 1));

        bd.getCollection("adoptantes").createIndex(Indexes.ascending("dni"), new IndexOptions().unique(true));
        bd.getCollection("adopciones").createIndex(Indexes.ascending("animalId"));
        bd.getCollection("adopciones").createIndex(Indexes.ascending("adoptanteId"));
    }

    public static void cerrar() {
        if (cliente != null) {
            cliente.close();
            cliente = null;
        }
    }
}
