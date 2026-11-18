package org.example.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.example.dao.RefugioDAO;
import org.example.modelo.Refugio;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * Prepara la base de datos al arrancar la aplicación.
 *
 * - Crea las colecciones (si no existen) mediante sus índices.
 * - Asegura los índices que usan las búsquedas.
 * - Crea el documento del refugio la primera vez.
 *
 * La operación es idempotente: se puede ejecutar en cada arranque sin efectos
 * secundarios.
 */
public final class Inicializador {

    private static final String COL_ANIMALES = "animales";
    private static final String COL_ADOPTANTES = "adoptantes";
    private static final String COL_ADOPCIONES = "adopciones";
    private static final String COL_REFUGIO = "refugio";

    private Inicializador() {
    }

    public static void preparar() {
        MongoDatabase bd = Conexion.bd();

        crearIndices(bd.getCollection(COL_ANIMALES),
                new IndexModel(Indexes.ascending("nombre")),
                new IndexModel(Indexes.ascending("especie")),
                new IndexModel(Indexes.ascending("estado")));

        crearIndices(bd.getCollection(COL_ADOPTANTES),
                new IndexModel(Indexes.ascending("dni"), new IndexOptions().unique(true)),
                new IndexModel(Indexes.ascending("apellidos")));

        crearIndices(bd.getCollection(COL_ADOPCIONES),
                new IndexModel(Indexes.ascending("animalId")),
                new IndexModel(Indexes.ascending("adoptanteId")),
                new IndexModel(Indexes.ascending("estado")),
                new IndexModel(Indexes.descending("fechaSolicitud")));

        crearRefugioPorDefecto(bd.getCollection(COL_REFUGIO));
    }

    private static void crearIndices(MongoCollection<Document> coleccion, IndexModel... indices) {
        coleccion.createIndexes(Arrays.asList(indices));
    }

    private static void crearRefugioPorDefecto(MongoCollection<Document> refugio) {
        if (refugio.countDocuments() == 0) {
            Refugio datos = new Refugio(
                    "Refugio de Animales",
                    "G12345678",
                    "Calle Mayor 1, 20001 Donostia",
                    "943 000 000",
                    "info@refugio.org",
                    LocalDate.of(2015, 1, 1));
            refugio.insertOne(RefugioDAO.aDocumento(datos));
        }
    }
}