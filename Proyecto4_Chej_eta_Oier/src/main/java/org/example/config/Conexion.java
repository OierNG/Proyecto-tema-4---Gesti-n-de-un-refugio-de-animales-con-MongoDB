package org.example.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/** Conexión única (Singleton) con MongoDB: un solo MongoClient compartido por todos los DAO. */
public final class Conexion {

    private static final String URI = "mongodb://localhost:27017";
    public static final String BASE_DATOS = "refugio";

    private static MongoClient cliente;
    private static MongoDatabase baseDatos;

    private Conexion() {
    }

    public static MongoDatabase bd() {
        if (baseDatos == null) {
            cliente = MongoClients.create(URI);
            baseDatos = cliente.getDatabase(BASE_DATOS);
        }
        return baseDatos;
    }

    public static void cerrar() {
        if (cliente != null) {
            cliente.close();
        }
        cliente = null;
        baseDatos = null;
    }
}