package org.example.vista;

import org.example.dao.RefugioDAO;
import org.example.modelo.Refugio;
import org.example.util.Consola;

public final class MenuRefugio extends Menu {

    private final RefugioDAO dao = new RefugioDAO();

    public MenuRefugio() {
        super("DATOS DEL REFUGIO", "Modificar datos");
    }

    @Override
    protected void cabecera() {
        Refugio refugio = dao.obtener();
        if (refugio == null) {
            Consola.info("Todavía no hay datos del refugio.");
            return;
        }
        Consola.info("Nombre.........: " + refugio.getNombre());
        Consola.info("CIF............: " + refugio.getCif());
        Consola.info("Dirección......: " + refugio.getDireccion());
        Consola.info("Teléfono.......: " + refugio.getTelefono());
        Consola.info("Email..........: " + refugio.getEmail());
        Consola.info("Fecha apertura.: " + Consola.texto(refugio.getFechaApertura()));
    }

    @Override
    protected void ejecutar(int opcion) {
        Refugio refugio = dao.obtener();
        Refugio actual = refugio == null ? new Refugio() : refugio;
        Consola.subtitulo("Modificar datos (ENTER mantiene el valor actual)");

        actual.setNombre(Consola.leerTextoOpcional("Nombre", actual.getNombre()));
        actual.setCif(Consola.leerTextoOpcional("CIF", actual.getCif()));
        actual.setDireccion(Consola.leerTextoOpcional("Dirección", actual.getDireccion()));
        actual.setTelefono(Consola.leerTextoOpcional("Teléfono", actual.getTelefono()));
        actual.setEmail(Consola.leerTextoOpcional("Email", actual.getEmail()));
        actual.setFechaApertura(Consola.leerFechaOpcional("Fecha de apertura (dd/MM/yyyy)", actual.getFechaApertura()));

        if (Consola.confirmar("¿Guardar los cambios?")) {
            dao.guardar(actual);
            Consola.ok("Datos del refugio actualizados.");
        } else {
            Consola.info("Cambios descartados.");
        }
    }
}
