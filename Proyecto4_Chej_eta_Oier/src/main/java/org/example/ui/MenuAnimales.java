package org.example.ui;

import org.example.model.Animal;
import org.example.model.EstadoAnimal;
import org.example.model.RegistroMedico;
import org.example.service.AnimalService;
import org.example.service.ExportacionService;
import org.example.util.Consola;

public class MenuAnimales extends MenuBase {

    private final AnimalService service;
    private final ExportacionService exportacionService;

    public MenuAnimales(AnimalService service, ExportacionService exportacionService) {
        super("GESTIÓN DE ANIMALES", "Volver");
        this.service = service;
        this.exportacionService = exportacionService;
    }

    @Override
    protected String[] opciones() {
        return new String[]{
                "Alta de animal",
                "Baja de animal",
                "Modificar animal",
                "Listar animales",
                "Ver ficha e historial médico",
                "Añadir registro médico",
                "Exportar animales (JSON / XML)"
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
            case 6 -> anadirRegistroMedico();
            case 7 -> exportar(exportacionService, service.listarTodos(), "animales", "animal");
        }
    }

    private void alta() {
        Animal animal = new Animal(
                Consola.leerTexto("Nombre: ", 50),
                Consola.leerTexto("Especie: ", 30),
                Consola.leerTexto("Raza: ", 50),
                Consola.leerEntero("Edad (años): ", 0, AnimalService.EDAD_MAXIMA),
                Consola.elegir("Sexo:", AnimalService.SEXOS),
                Consola.leerTexto("Descripción: ", 200),
                Consola.leerFecha("Fecha de ingreso"),
                Consola.elegir("Estado:", EstadoAnimal.values()));

        service.alta(animal);
        exito("Animal dado de alta con el id " + animal.getId() + ".");
    }

    private void baja() {
        Animal animal = service.buscarPorId(leerId("animal"));
        System.out.println(animal);

        boolean definitiva = elegirBajaDefinitiva();
        if (Consola.confirmar("¿Seguro que quieres darlo de baja?")) {
            service.baja(animal.getId(), definitiva);
            exito(definitiva ? "Animal eliminado." : "Animal marcado como eliminado.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void modificar() {
        Animal animal = service.buscarPorId(leerId("animal"));
        System.out.println("Pulsa Enter para mantener el valor actual.");

        animal.setNombre(Consola.leerTextoOpcional("Nombre", animal.getNombre(), 50));
        animal.setEspecie(Consola.leerTextoOpcional("Especie", animal.getEspecie(), 30));
        animal.setRaza(Consola.leerTextoOpcional("Raza", animal.getRaza(), 50));
        animal.setEdad(Consola.leerEnteroOpcional("Edad", animal.getEdad(), 0, AnimalService.EDAD_MAXIMA));
        animal.setSexo(Consola.elegirOpcional("Sexo:", AnimalService.SEXOS, animal.getSexo()));
        animal.setDescripcion(Consola.leerTextoOpcional("Descripción", animal.getDescripcion(), 200));
        animal.setFechaIngreso(Consola.leerFechaOpcional("Fecha de ingreso", animal.getFechaIngreso()));
        animal.setEstado(Consola.elegirOpcional("Estado:", EstadoAnimal.values(), animal.getEstado()));

        service.modificar(animal);
        exito("Animal modificado.");
    }

    private void verFicha() {
        Animal animal = service.buscarPorId(leerId("animal"));
        System.out.println(animal);
        System.out.println("Descripción: " + animal.getDescripcion());
        System.out.println("\nHistorial médico:");
        mostrarLista(animal.getHistorialMedico());
    }

    private void anadirRegistroMedico() {
        Animal animal = service.buscarPorId(leerId("animal"));
        System.out.println(animal);

        RegistroMedico registro = new RegistroMedico(
                Consola.leerFecha("Fecha"),
                Consola.leerTexto("Tipo (vacuna, revisión, cirugía...): ", 30),
                Consola.leerTexto("Descripción: ", 200),
                Consola.leerTexto("Veterinario: ", 50));

        service.anadirRegistroMedico(animal.getId(), registro);
        exito("Registro médico añadido.");
    }
}
