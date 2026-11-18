package org.example.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Conexión única con el servidor MongoDB.
 *
 * Se abre una sola vez y se reutiliza desde todos los DAO. Así la aplicación
 * mantiene un único pool de conexiones, tal como recomienda el driver.
 */
public final class Conexion {

    /** URI del servidor local de MongoDB Community (puerto por defecto 27017). */
    private static final String URI = "mongodb://localhost:27017";

    /** Nombre de la base de datos del proyecto. */
    public static final String BASE_DATOS = "refugio";

    private static MongoClient cliente;
    private static MongoDatabase baseDatos;

    private Conexion() {
    }

    /** Devuelve la base de datos, abriendo la conexión la primera vez. */
    public static MongoDatabase bd() {
        if (baseDatos == null) {
            cliente = MongoClients.create(URI);
            baseDatos = cliente.getDatabase(BASE_DATOS);
        }
        return baseDatos;
    }

    /** Cierra la conexión al salir de la aplicación. */
    public static void cerrar() {
        if (cliente != null) {
            cliente.close();
        }
        cliente = null;
        baseDatos = null;
    }
}