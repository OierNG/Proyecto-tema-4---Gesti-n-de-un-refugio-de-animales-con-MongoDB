package org.example;

import com.mongodb.MongoException;
import org.bson.Document;
import org.example.config.Conexion;
import org.example.config.Inicializador;
import org.example.dao.RefugioDAO;
import org.example.modelo.Refugio;
import org.example.util.Consola;
import org.example.vista.MenuRefugio;

/**
 * Aplicación de gestión de un refugio de animales con MongoDB.
 *
 * Menú principal en consola; cada apartado tiene su propio menú y su clase DAO.
 */
public class App {

    public static void main(String[] args) {
        if (!conectar()) {
            return;
        }
        try {
            menuPrincipal();
        } catch (MongoException e) {
            Consola.error("Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            Consola.error("Error inesperado: " + e.getMessage());
        } finally {
            Conexion.cerrar();
        }
        Consola.info("");
        Consola.info("Aplicación cerrada.");
    }

    /** Comprueba la conexión y prepara el esquema de la base de datos. */
    private static boolean conectar() {
        try {
            Conexion.bd().runCommand(new Document("ping", 1));
            Inicializador.preparar();
            Consola.ok("Conexión establecida con MongoDB (base de datos '" + Conexion.BASE_DATOS + "').");
            return true;
        } catch (MongoException e) {
            Consola.error("No se ha podido conectar con MongoDB en localhost:27017.");
            Consola.info("Comprueba que MongoDB Community Server está arrancado.");
            Consola.info("Detalle: " + e.getMessage());
            return false;
        }
    }

    /** Bucle del menú principal. */
    private static void menuPrincipal() {
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            int opcion = Consola.leerEntero("Opción: ", 0, 6);
            try {
                switch (opcion) {
                    case 1 -> enConstruccion("Animales");
                    case 2 -> enConstruccion("Adoptantes");
                    case 3 -> enConstruccion("Adopciones");
                    case 4 -> new MenuRefugio().mostrar();
                    case 5 -> enConstruccion("Exportar datos");
                    case 6 -> enConstruccion("Metadatos");
                    case 0 -> salir = true;
                    default -> { }
                }
            } catch (MongoException e) {
                Consola.error("Error de base de datos: " + e.getMessage());
                Consola.pausa();
            } catch (Exception e) {
                Consola.error("Error inesperado: " + e.getMessage());
                Consola.pausa();
            }
        }
    }

    /** Cabecera con los datos más relevantes del refugio. */
    private static void mostrarMenu() {
        Refugio refugio = new RefugioDAO().obtener();
        Consola.titulo(refugio == null ? "REFUGIO DE ANIMALES" : refugio.getNombre());

        if (refugio != null) {
            Consola.info("CIF: " + refugio.getCif() + "   |   Teléfono: " + refugio.getTelefono()
                    + "   |   Email: " + refugio.getEmail());
            Consola.info("Dirección: " + refugio.getDireccion()
                    + "   |   Apertura: " + Consola.texto(refugio.getFechaApertura()));
        }

        Consola.info("");
        Consola.info("  1. Animales");
        Consola.info("  2. Adoptantes");
        Consola.info("  3. Adopciones");
        Consola.info("  4. Datos del refugio");
        Consola.info("  5. Exportar datos (XML / JSON)");
        Consola.info("  6. Metadatos de la base de datos");
        Consola.info("  0. Salir");
    }

    private static void enConstruccion(String apartado) {
        Consola.subtitulo(apartado);
        Consola.info("En construcción: se implementa en el siguiente commit.");
        Consola.pausa();
    }
}