package org.example.vista;

import org.example.dao.AnimalDAO;
import org.example.modelo.Animal;
import org.example.modelo.EstadoAnimal;
import org.example.modelo.RegistroMedico;
import org.example.util.Consola;

import java.time.LocalDate;
import java.util.List;

public final class MenuAnimales extends Menu {

    private static final String[] SEXOS = {"Macho", "Hembra"};

    private final AnimalDAO dao = new AnimalDAO();

    public MenuAnimales() {
        super("GESTIÓN DE ANIMALES",
                "Listar animales",
                "Ver ficha e historial médico",
                "Alta de animal",
                "Modificar animal",
                "Añadir registro médico",
                "Baja de animal");
    }

    @Override
    protected void ejecutar(int opcion) {
        switch (opcion) {
            case 1 -> listar();
            case 2 -> verFicha();
            case 3 -> alta();
            case 4 -> modificar();
            case 5 -> anadirRegistroMedico();
            case 6 -> baja();
        }
    }

    private void listar() {
        List<Animal> animales = dao.listar();
        Consola.subtitulo("Animales del refugio (" + animales.size() + ")");
        if (animales.isEmpty()) {
            Consola.info("No hay animales registrados.");
            return;
        }
        Consola.info(String.format("%-4s %-12s %-10s %-15s %-5s %-7s %-11s %s",
                "ID", "NOMBRE", "ESPECIE", "RAZA", "EDAD", "SEXO", "INGRESO", "ESTADO"));
        for (Animal a : animales) {
            Consola.info(String.format("%-4d %-12s %-10s %-15s %-5d %-7s %-11s %s",
                    a.getId(), a.getNombre(), a.getEspecie(), a.getRaza(), a.getEdad(),
                    a.getSexo(), Consola.texto(a.getFechaIngreso()), a.getEstado()));
        }
    }

    private void verFicha() {
        Animal animal = pedirAnimal();
        if (animal == null) {
            return;
        }
        Consola.subtitulo("Ficha del animal " + animal.getId());
        Consola.info("Nombre.......: " + animal.getNombre());
        Consola.info("Especie......: " + animal.getEspecie());
        Consola.info("Raza.........: " + animal.getRaza());
        Consola.info("Edad.........: " + animal.getEdad() + " años");
        Consola.info("Sexo.........: " + animal.getSexo());
        Consola.info("Descripción..: " + animal.getDescripcion());
        Consola.info("Ingreso......: " + Consola.texto(animal.getFechaIngreso()));
        Consola.info("Estado.......: " + animal.getEstado());

        Consola.subtitulo("Historial médico");
        if (animal.getHistorialMedico().isEmpty()) {
            Consola.info("Sin registros médicos.");
        }
        for (RegistroMedico r : animal.getHistorialMedico()) {
            Consola.info(Consola.texto(r.getFecha()) + " | " + r.getTipo() + " | "
                    + r.getDescripcion() + " | Veterinario: " + r.getVeterinario());
        }
    }

    private void alta() {
        Consola.subtitulo("Alta de animal");
        Animal animal = new Animal();
        animal.setNombre(Consola.leerTexto("Nombre: "));
        animal.setEspecie(Consola.leerTexto("Especie: "));
        animal.setRaza(Consola.leerTexto("Raza: "));
        animal.setEdad(Consola.leerEntero("Edad (años): ", 0, 40));
        animal.setSexo(Consola.elegir("Sexo:", SEXOS, null));
        animal.setDescripcion(Consola.leerTexto("Descripción: "));
        animal.setFechaIngreso(Consola.leerFecha("Fecha de ingreso (dd/MM/yyyy): "));
        animal.setEstado(Consola.elegir("Estado:", EstadoAnimal.values(), null));

        dao.insertar(animal);
        Consola.ok("Animal registrado con el ID " + animal.getId() + ".");
    }

    private void modificar() {
        Animal animal = pedirAnimal();
        if (animal == null) {
            return;
        }
        Consola.subtitulo("Modificar animal (ENTER mantiene el valor actual)");
        animal.setNombre(Consola.leerTextoOpcional("Nombre", animal.getNombre()));
        animal.setEspecie(Consola.leerTextoOpcional("Especie", animal.getEspecie()));
        animal.setRaza(Consola.leerTextoOpcional("Raza", animal.getRaza()));
        animal.setEdad(Consola.leerEnteroOpcional("Edad", animal.getEdad(), 0, 40));
        animal.setSexo(Consola.elegir("Sexo:", SEXOS, animal.getSexo()));
        animal.setDescripcion(Consola.leerTextoOpcional("Descripción", animal.getDescripcion()));
        animal.setFechaIngreso(Consola.leerFechaOpcional("Fecha de ingreso", animal.getFechaIngreso()));
        animal.setEstado(Consola.elegir("Estado:", EstadoAnimal.values(), animal.getEstado()));

        dao.actualizar(animal);
        Consola.ok("Animal actualizado.");
    }

    private void anadirRegistroMedico() {
        Animal animal = pedirAnimal();
        if (animal == null) {
            return;
        }
        Consola.subtitulo("Nuevo registro médico para " + animal.getNombre());
        RegistroMedico registro = new RegistroMedico(
                Consola.leerFecha("Fecha (dd/MM/yyyy): "),
                Consola.leerTexto("Tipo (vacuna, revisión, cirugía...): "),
                Consola.leerTexto("Descripción: "),
                Consola.leerTexto("Veterinario: "));

        dao.anadirRegistroMedico(animal.getId(), registro);
        Consola.ok("Registro médico añadido.");
    }

    private void baja() {
        Animal animal = pedirAnimal();
        if (animal == null) {
            return;
        }
        Consola.subtitulo("Baja de " + animal.getNombre());
        String[] tipos = {"Marcar como eliminado (se conserva en la BBDD)", "Eliminar definitivamente"};
        String tipo = Consola.elegir("Tipo de baja:", tipos, null);

        if (tipo.equals(tipos[1]) && dao.tieneAdopciones(animal.getId())) {
            Consola.error("El animal tiene adopciones asociadas: no se puede eliminar, solo marcar como eliminado.");
            return;
        }
        if (!Consola.confirmar("¿Confirmas la baja?")) {
            Consola.info("Operación cancelada.");
            return;
        }
        if (tipo.equals(tipos[0])) {
            dao.marcarEliminado(animal.getId());
        } else {
            dao.eliminar(animal.getId());
        }
        Consola.ok("Animal dado de baja.");
    }

    private Animal pedirAnimal() {
        int id = Consola.leerEntero("ID del animal: ", 1, Integer.MAX_VALUE);
        Animal animal = dao.buscarPorId(id);
        if (animal == null) {
            Consola.error("No existe ningún animal con el ID " + id + ".");
        }
        return animal;
    }
}
