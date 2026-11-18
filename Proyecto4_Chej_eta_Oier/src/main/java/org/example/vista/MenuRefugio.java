package org.example.vista;

import org.example.dao.RefugioDAO;
import org.example.modelo.Refugio;
import org.example.util.Consola;

import java.time.LocalDate;

/**
 * Menú de consulta y modificación de los datos del refugio.
 */
public final class MenuRefugio {

    private final RefugioDAO dao = new RefugioDAO();

    public void mostrar() {
        boolean volver = false;
        while (!volver) {
            Consola.titulo("DATOS DEL REFUGIO");

            Refugio refugio = dao.obtener();
            if (refugio == null) {
                Consola.info("Todavía no hay datos del refugio.");
            } else {
                mostrarDatos(refugio);
            }

            Consola.info("");
            Consola.info("  1. Modificar datos");
            Consola.info("  0. Volver");

            int opcion = Consola.leerEntero("Opción: ", 0, 1);
            switch (opcion) {
                case 1 -> modificar(refugio);
                case 0 -> volver = true;
            }
        }
    }

    private void mostrarDatos(Refugio refugio) {
        Consola.subtitulo("Información del refugio");
        Consola.info("Nombre.........: " + refugio.getNombre());
        Consola.info("CIF............: " + refugio.getCif());
        Consola.info("Dirección......: " + refugio.getDireccion());
        Consola.info("Teléfono.......: " + refugio.getTelefono());
        Consola.info("Email..........: " + refugio.getEmail());
        Consola.info("Fecha apertura.: " + Consola.texto(refugio.getFechaApertura()));
    }

    private void modificar(Refugio refugio) {
        Refugio actual = refugio == null ? new Refugio() : refugio;
        Consola.subtitulo("Modificar datos (ENTER mantiene el valor actual)");

        actual.setNombre(Consola.leerTextoOpcional("Nombre", actual.getNombre()));
        actual.setCif(Consola.leerTextoOpcional("CIF", actual.getCif()));
        actual.setDireccion(Consola.leerTextoOpcional("Dirección", actual.getDireccion()));
        actual.setTelefono(Consola.leerTextoOpcional("Teléfono", actual.getTelefono()));
        actual.setEmail(Consola.leerTextoOpcional("Email", actual.getEmail()));
        LocalDate apertura = Consola.leerFechaOpcional("Fecha de apertura (dd/MM/yyyy)", actual.getFechaApertura());
        actual.setFechaApertura(apertura);

        if (Consola.confirmar("¿Guardar los cambios?")) {
            dao.guardar(actual);
            Consola.ok("Datos del refugio actualizados.");
        } else {
            Consola.info("Cambios descartados.");
        }
        Consola.pausa();
    }
}