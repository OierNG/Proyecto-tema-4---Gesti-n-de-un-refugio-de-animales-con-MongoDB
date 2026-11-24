package org.example.vista;

import com.mongodb.MongoException;
import org.example.dao.BaseDAO;
import org.example.util.Consola;

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

    protected <T> T pedirPorId(BaseDAO<T> dao, String entidad) {
        int id = Consola.leerEntero(
                "ID de " + entidad + ": ",
                1,
                Integer.MAX_VALUE
        );

        T objeto = dao.buscarPorId(id);

        if (objeto == null) {
            Consola.error("No existe " + entidad + " con el ID " + id + ".");
        }

        return objeto;
    }

    protected boolean baja(
            BaseDAO<?> dao,
            int id,
            String nombre,
            boolean tieneReferencias) {

        Consola.subtitulo("Baja de " + nombre);

        String[] tipos = {
                "Marcar como eliminado (se conserva en la BBDD)",
                "Eliminar definitivamente"
        };

        boolean definitiva = Consola.elegir(
                "Tipo de baja:",
                tipos,
                null
        ).equals(tipos[1]);

        if (definitiva && tieneReferencias) {
            Consola.error(
                    "Tiene adopciones asociadas: no se puede eliminar, solo marcar como eliminado."
            );
            return false;
        }

        if (!Consola.confirmar("¿Confirmas la baja?")) {
            Consola.info("Operación cancelada.");
            return false;
        }

        if (definitiva) {
            dao.eliminar(id);
        } else {
            dao.marcarEliminado(id);
        }

        Consola.ok("Baja realizada.");
        return true;
    }
}