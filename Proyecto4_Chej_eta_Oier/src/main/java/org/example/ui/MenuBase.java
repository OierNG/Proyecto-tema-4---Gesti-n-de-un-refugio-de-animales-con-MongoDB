package org.example.ui;

import org.example.exception.RefugioException;
import org.example.service.ExportacionService;
import org.example.util.Consola;

import java.nio.file.Path;
import java.util.List;

// Plantilla común de todos los menús: muestra opciones, lee la elegida y captura cualquier error
public abstract class MenuBase {

    private final String titulo;
    private final String textoSalida;

    protected MenuBase(String titulo, String textoSalida) {
        this.titulo = titulo;
        this.textoSalida = textoSalida;
    }

    protected abstract String[] opciones();

    protected abstract void ejecutarOpcion(int opcion);

    // Información que se muestra bajo el título (el menú principal muestra los datos del refugio)
    protected void cabecera() {
    }

    // Los submenús pausan tras cada operación; el menú principal lo desactiva
    protected boolean pausarDespuesDeOpcion() {
        return true;
    }

    public void mostrar() {
        while (true) {
            System.out.println("\n========== " + titulo + " ==========");
            try {
                cabecera();
            } catch (RefugioException e) {
                System.out.println("  ✗ " + e.getMessage());
            }
            String[] opciones = opciones();
            for (int i = 0; i < opciones.length; i++) {
                System.out.println((i + 1) + ". " + opciones[i]);
            }
            System.out.println("0. " + textoSalida);

            int opcion = Consola.leerEntero("Elige una opción: ", 0, opciones.length);
            if (opcion == 0) {
                return;
            }

            // Cualquier error se muestra y se vuelve al menú: el programa nunca se cierra de golpe
            try {
                ejecutarOpcion(opcion);
            } catch (RefugioException e) {
                System.out.println("  ✗ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("  ✗ Error inesperado: " + e.getMessage());
            }

            if (pausarDespuesDeOpcion()) {
                Consola.pausa();
            }
        }
    }

    protected <T> void mostrarLista(List<T> lista) {
        if (lista.isEmpty()) {
            System.out.println("No se han encontrado resultados.");
            return;
        }
        lista.forEach(elemento -> System.out.println("  " + elemento));
        System.out.println("Total: " + lista.size());
    }

    protected int leerId(String entidad) {
        return Consola.leerEntero("Id de " + entidad + ": ", 1, Integer.MAX_VALUE);
    }

    // El enunciado permite eliminar el documento o solo marcarlo como eliminado
    protected boolean elegirBajaDefinitiva() {
        return Consola.leerEntero("Tipo de baja (1 = marcar como eliminado, 2 = eliminar definitivamente): ", 1, 2) == 2;
    }

    protected void exportar(ExportacionService exportacionService, List<?> lista, String nombre, String elemento) {
        if (lista.isEmpty()) {
            System.out.println("No hay datos que exportar.");
            return;
        }
        int formato = Consola.leerEntero("Formato (1 = JSON, 2 = XML): ", 1, 2);
        Path fichero = formato == 1
                ? exportacionService.exportarJSON(lista, nombre)
                : exportacionService.exportarXML(lista, nombre, elemento);
        exito("Exportado en: " + fichero.toAbsolutePath());
    }

    protected void exito(String mensaje) {
        System.out.println("  ✓ " + mensaje);
    }
}
