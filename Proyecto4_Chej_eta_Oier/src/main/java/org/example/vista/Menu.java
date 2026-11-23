package org.example.vista;

import com.mongodb.MongoException;
import org.example.util.Consola;

/**
 * Bucle común de todos los submenús: muestra las opciones, lee la elegida
 * y controla los errores para que nunca se cierre la aplicación.
 */
public abstract class Menu {

    private final String titulo;
    private final String[] opciones;

    protected Menu(String titulo, String... opciones) {
        this.titulo = titulo;
        this.opciones = opciones;
    }

    public void mostrar() {
        while (true) {
            Consola.titulo(titulo);
            cabecera();
            Consola.info("");
            for (int i = 0; i < opciones.length; i++) {
                Consola.info("  " + (i + 1) + ". " + opciones[i]);
            }
            Consola.info("  0. Volver");

            int opcion = Consola.leerEntero("Opción: ", 0, opciones.length);
            if (opcion == 0) {
                return;
            }
            try {
                ejecutar(opcion);
            } catch (MongoException e) {
                Consola.error("Error de base de datos: " + e.getMessage());
            } catch (Exception e) {
                Consola.error("Error inesperado: " + e.getMessage());
            }
            Consola.pausa();
        }
    }

    protected void cabecera() {
    }

    protected abstract void ejecutar(int opcion);
}
