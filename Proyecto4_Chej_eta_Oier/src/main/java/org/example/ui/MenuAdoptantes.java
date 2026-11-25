package org.example.ui;

import org.example.exception.ValidacionException;
import org.example.model.Adoptante;
import org.example.model.Preferencias;
import org.example.service.AdopcionService;
import org.example.service.AdoptanteService;
import org.example.service.ExportacionService;
import org.example.util.Consola;

import java.time.LocalDate;

public class MenuAdoptantes extends MenuBase {

    private static final String ERROR_DNI = "DNI/NIE no válido (ejemplos: 12345678A, X1234567B).";
    private static final String ERROR_EMAIL = "Email no válido (ejemplo: nombre@correo.com).";
    private static final String ERROR_TELEFONO = "Teléfono no válido: entre 9 y 15 dígitos.";

    private final AdoptanteService service;
    private final AdopcionService adopcionService;
    private final ExportacionService exportacionService;

    public MenuAdoptantes(AdoptanteService service, AdopcionService adopcionService, ExportacionService exportacionService) {
        super("GESTIÓN DE ADOPTANTES", "Volver");
        this.service = service;
        this.adopcionService = adopcionService;
        this.exportacionService = exportacionService;
    }

    @Override
    protected String[] opciones() {
        return new String[]{
                "Alta de adoptante",
                "Baja de adoptante",
                "Modificar adoptante",
                "Listar adoptantes",
                "Ver ficha",
                "Ver solicitudes y adopciones",
                "Exportar adoptantes (JSON / XML)"
        };
    }

    @Override
    protected void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> alta();
            case 2 -> baja();
            case 3 -> modificar();
            case 4 -> mostrarLista(service.listarTodos());
            case 5 -> verFicha();
            case 6 -> verAdopciones();
            case 7 -> exportar(exportacionService, service.listarTodos(), "adoptantes", "adoptante");
        }
    }

    private void alta() {
        String dni = Consola.leerConFormato("DNI/NIE: ", AdoptanteService.PATRON_DNI, ERROR_DNI).toUpperCase();
        if (service.existeDni(dni, 0)) {
            throw new ValidacionException("Ya existe un adoptante con el DNI/NIE " + dni + ".");
        }

        String nombre = Consola.leerTexto("Nombre: ", 50);
        String apellidos = Consola.leerTexto("Apellidos: ", 100);
        String telefono = Consola.leerConFormato("Teléfono: ", AdoptanteService.PATRON_TELEFONO, ERROR_TELEFONO);
        String direccion = Consola.leerTexto("Dirección: ", 150);
        String email = Consola.leerConFormato("Email: ", AdoptanteService.PATRON_EMAIL, ERROR_EMAIL);

        System.out.println("Preferencias de adopción:");
        Preferencias preferencias = new Preferencias(
                Consola.leerTexto("  Especie (perro, gato, cualquiera...): ", 30),
                Consola.elegir("  Sexo:", AdoptanteService.SEXOS_PREFERIDOS),
                Consola.leerEntero("  Edad máxima (años): ", 0, 40));

        Adoptante adoptante = new Adoptante(dni, nombre, apellidos, telefono, direccion, email, LocalDate.now(), preferencias);
        service.alta(adoptante);
        exito("Adoptante dado de alta con el id " + adoptante.getId() + ".");
    }

    private void baja() {
        Adoptante adoptante = service.buscarPorId(leerId("adoptante"));
        System.out.println(adoptante);

        boolean definitiva = elegirBajaDefinitiva();
        if (Consola.confirmar("¿Seguro que quieres darlo de baja?")) {
            service.baja(adoptante.getId(), definitiva);
            exito(definitiva ? "Adoptante eliminado." : "Adoptante marcado como eliminado.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void modificar() {
        Adoptante adoptante = service.buscarPorId(leerId("adoptante"));
        System.out.println("Pulsa Enter para mantener el valor actual.");

        adoptante.setDni(Consola.leerConFormatoOpcional("DNI/NIE", adoptante.getDni(), AdoptanteService.PATRON_DNI, ERROR_DNI));
        adoptante.setNombre(Consola.leerTextoOpcional("Nombre", adoptante.getNombre(), 50));
        adoptante.setApellidos(Consola.leerTextoOpcional("Apellidos", adoptante.getApellidos(), 100));
        adoptante.setTelefono(Consola.leerConFormatoOpcional("Teléfono", adoptante.getTelefono(), AdoptanteService.PATRON_TELEFONO, ERROR_TELEFONO));
        adoptante.setDireccion(Consola.leerTextoOpcional("Dirección", adoptante.getDireccion(), 150));
        adoptante.setEmail(Consola.leerConFormatoOpcional("Email", adoptante.getEmail(), AdoptanteService.PATRON_EMAIL, ERROR_EMAIL));

        Preferencias p = adoptante.getPreferencias();
        System.out.println("Preferencias de adopción:");
        p.setEspecie(Consola.leerTextoOpcional("  Especie", p.getEspecie(), 30));
        p.setSexo(Consola.elegirOpcional("  Sexo:", AdoptanteService.SEXOS_PREFERIDOS, p.getSexo()));
        p.setEdadMaxima(Consola.leerEnteroOpcional("  Edad máxima", p.getEdadMaxima(), 0, 40));

        service.modificar(adoptante);
        exito("Adoptante modificado.");
    }

    private void verFicha() {
        Adoptante adoptante = service.buscarPorId(leerId("adoptante"));
        System.out.println(adoptante);
        System.out.println("Dirección: " + adoptante.getDireccion());
        System.out.println("Preferencias: " + adoptante.getPreferencias());
    }

    private void verAdopciones() {
        Adoptante adoptante = service.buscarPorId(leerId("adoptante"));
        System.out.println("Solicitudes y adopciones de " + adoptante.getNombre() + " " + adoptante.getApellidos() + ":");
        mostrarLista(adopcionService.buscarPorAdoptante(adoptante.getId()));
    }
}
