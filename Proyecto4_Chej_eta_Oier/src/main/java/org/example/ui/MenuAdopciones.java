package org.example.ui;

import org.example.model.Adopcion;
import org.example.model.EstadoAdopcion;
import org.example.model.EstadoAnimal;
import org.example.model.Seguimiento;
import org.example.service.AdopcionService;
import org.example.service.ExportacionService;
import org.example.util.Consola;

public class MenuAdopciones extends MenuBase {

    private final AdopcionService service;
    private final ExportacionService exportacionService;

    public MenuAdopciones(AdopcionService service, ExportacionService exportacionService) {
        super("GESTIÓN DE ADOPCIONES", "Volver");
        this.service = service;
        this.exportacionService = exportacionService;
    }

    @Override
    protected String[] opciones() {
        return new String[]{
                "Registrar solicitud",
                "Baja de adopción",
                "Modificar adopción",
                "Listar adopciones",
                "Ver detalle (animal y adoptante)",
                "Aprobar / rechazar solicitud",
                "Añadir seguimiento",
                "Exportar adopciones (JSON / XML)"
        };
    }

    @Override
    protected void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> registrarSolicitud();
            case 2 -> baja();
            case 3 -> modificar();
            case 4 -> mostrarLista(service.listarTodos());
            case 5 -> verDetalle();
            case 6 -> resolver();
            case 7 -> anadirSeguimiento();
            case 8 -> exportar(exportacionService, service.listarTodos(), "adopciones", "adopcion");
        }
    }

    private void registrarSolicitud() {
        int animalId = leerId("animal");
        int adoptanteId = leerId("adoptante");
        String observaciones = Consola.leerTextoOpcional("Observaciones", "", 200);

        Adopcion adopcion = service.registrarSolicitud(animalId, adoptanteId, observaciones);
        exito("Solicitud registrada con el id " + adopcion.getId() + ". El animal pasa a Reservado.");
    }

    private void baja() {
        Adopcion adopcion = service.buscarPorId(leerId("adopción"));
        System.out.println(adopcion);

        boolean definitiva = elegirBajaDefinitiva();
        if (Consola.confirmar("¿Seguro que quieres darla de baja?")) {
            service.baja(adopcion.getId(), definitiva);
            exito(definitiva ? "Adopción eliminada." : "Adopción marcada como eliminada.");
            if (adopcion.getEstado() == EstadoAdopcion.PENDIENTE) {
                System.out.println("El animal vuelve a estar Disponible.");
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void modificar() {
        Adopcion adopcion = service.buscarPorId(leerId("adopción"));
        System.out.println("Pulsa Enter para mantener el valor actual.");

        adopcion.setFechaSolicitud(Consola.leerFechaOpcional("Fecha de solicitud", adopcion.getFechaSolicitud()));
        adopcion.setObservaciones(Consola.leerTextoOpcional("Observaciones", adopcion.getObservaciones(), 200));

        service.modificar(adopcion);
        exito("Adopción modificada.");
    }

    private void verDetalle() {
        Adopcion adopcion = service.buscarPorId(leerId("adopción"));
        System.out.println(adopcion);
        System.out.println("Observaciones: " + adopcion.getObservaciones());
        System.out.println("Animal:    " + service.animalDe(adopcion).map(String::valueOf).orElse("eliminado"));
        System.out.println("Adoptante: " + service.adoptanteDe(adopcion).map(String::valueOf).orElse("eliminado"));
        System.out.println("\nSeguimiento:");
        mostrarLista(adopcion.getSeguimiento());
    }

    private void resolver() {
        Adopcion adopcion = service.buscarPorId(leerId("adopción"));
        System.out.println(adopcion);

        EstadoAdopcion resolucion = Consola.elegir("Resolución:",
                new EstadoAdopcion[]{EstadoAdopcion.APROBADA, EstadoAdopcion.RECHAZADA});
        EstadoAnimal estadoAnimal = service.resolver(adopcion.getId(), resolucion);
        exito("Solicitud " + resolucion + ". El animal pasa a " + estadoAnimal + ".");
    }

    private void anadirSeguimiento() {
        Adopcion adopcion = service.buscarPorId(leerId("adopción"));
        System.out.println(adopcion);

        Seguimiento seguimiento = new Seguimiento(
                Consola.leerFecha("Fecha"),
                Consola.leerTexto("Descripción: ", 200));
        service.anadirSeguimiento(adopcion.getId(), seguimiento);
        exito("Seguimiento añadido.");
    }
}
