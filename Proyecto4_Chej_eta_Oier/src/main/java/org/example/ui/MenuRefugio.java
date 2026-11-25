package org.example.ui;

import org.example.model.Refugio;
import org.example.service.RefugioService;
import org.example.util.Consola;

public class MenuRefugio extends MenuBase {

    private final RefugioService service;

    public MenuRefugio(RefugioService service) {
        super("DATOS DEL REFUGIO", "Volver");
        this.service = service;
    }

    @Override
    protected String[] opciones() {
        return new String[]{
                "Ver datos del refugio",
                "Modificar datos del refugio"
        };
    }

    @Override
    protected void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> System.out.println(service.obtener());
            case 2 -> modificar();
        }
    }

    private void modificar() {
        Refugio refugio = service.obtener();
        System.out.println("Pulsa Enter para mantener el valor actual.");

        refugio.setNombre(Consola.leerTextoOpcional("Nombre", refugio.getNombre(), 100));
        refugio.setCif(Consola.leerTextoOpcional("CIF", refugio.getCif(), 9));
        refugio.setDireccion(Consola.leerTextoOpcional("Dirección", refugio.getDireccion(), 150));
        refugio.setTelefono(Consola.leerTextoOpcional("Teléfono", refugio.getTelefono(), 15));
        refugio.setEmail(Consola.leerTextoOpcional("Email", refugio.getEmail(), 100));
        refugio.setFechaApertura(Consola.leerFechaOpcional("Fecha de apertura", refugio.getFechaApertura()));

        service.modificar(refugio);
        exito("Datos del refugio modificados.");
    }
}
