package org.example.vista;

import org.example.dao.AdopcionDAO;
import org.example.dao.AdoptanteDAO;
import org.example.dao.AnimalDAO;
import org.example.modelo.Adopcion;
import org.example.modelo.Adoptante;
import org.example.modelo.Animal;
import org.example.modelo.EstadoAdopcion;
import org.example.modelo.EstadoAnimal;
import org.example.modelo.Seguimiento;
import org.example.util.Consola;

import java.time.LocalDate;
import java.util.List;

/** Al cambiar el estado de una adopción también se actualiza el estado del animal. */
public final class MenuAdopciones extends Menu {

    private final AdopcionDAO dao = new AdopcionDAO();
    private final AnimalDAO animalDAO = new AnimalDAO();
    private final AdoptanteDAO adoptanteDAO = new AdoptanteDAO();

    public MenuAdopciones() {
        super("GESTIÓN DE ADOPCIONES",
                "Listar adopciones",
                "Ver detalle (animal y adoptante)",
                "Registrar solicitud",
                "Modificar adopción",
                "Aprobar / rechazar solicitud",
                "Añadir seguimiento",
                "Baja de adopción");
    }

    @Override
    protected void ejecutar(int opcion) {
        switch (opcion) {
            case 1 -> listar();
            case 2 -> verDetalle();
            case 3 -> registrarSolicitud();
            case 4 -> modificar();
            case 5 -> resolver();
            case 6 -> anadirSeguimiento();
            case 7 -> baja();
        }
    }

    private void listar() {
        List<Adopcion> adopciones = dao.listar();
        Consola.subtitulo("Adopciones (" + adopciones.size() + ")");
        imprimir(adopciones);
    }

    /** Tabla de adopciones. También la usa MenuAdoptantes. */
    public static void imprimir(List<Adopcion> adopciones) {
        if (adopciones.isEmpty()) {
            Consola.info("No hay adopciones registradas.");
            return;
        }
        Consola.info(String.format("%-4s %-14s %-28s %-11s %-11s %s",
                "ID", "ANIMAL", "ADOPTANTE", "SOLICITUD", "RESOLUCIÓN", "ESTADO"));
        for (Adopcion a : adopciones) {
            Consola.info(String.format("%-4d %-14s %-28s %-11s %-11s %s",
                    a.getId(), nombreAnimal(a.getAnimalId()), nombreAdoptante(a.getAdoptanteId()),
                    Consola.texto(a.getFechaSolicitud()), Consola.texto(a.getFechaResolucion()), a.getEstado()));
        }
    }

    private void verDetalle() {
        Adopcion a = pedirPorId(dao, "adopción");
        if (a == null) {
            return;
        }
        Consola.subtitulo("Adopción " + a.getId());
        Consola.info("Solicitud....: " + Consola.texto(a.getFechaSolicitud()));
        Consola.info("Resolución...: " + Consola.texto(a.getFechaResolucion()));
        Consola.info("Estado.......: " + a.getEstado());
        Consola.info("Observaciones: " + a.getObservaciones());

        Consola.subtitulo("Animal");
        Animal animal = animalDAO.buscarPorId(a.getAnimalId());
        if (animal == null) {
            Consola.info("El animal " + a.getAnimalId() + " ha sido eliminado.");
        } else {
            Consola.info(animal.getId() + " - " + animal.getNombre() + " (" + animal.getEspecie() + ", "
                    + animal.getRaza() + ", " + animal.getEdad() + " años) - " + animal.getEstado());
        }

        Consola.subtitulo("Adoptante");
        Adoptante adoptante = adoptanteDAO.buscarPorId(a.getAdoptanteId());
        if (adoptante == null) {
            Consola.info("El adoptante " + a.getAdoptanteId() + " ha sido eliminado.");
        } else {
            Consola.info(adoptante.getId() + " - " + adoptante.getNombreCompleto() + " (" + adoptante.getDni()
                    + ") - Tel: " + adoptante.getTelefono() + " - " + adoptante.getEmail());
        }

        Consola.subtitulo("Seguimiento");
        if (a.getSeguimiento().isEmpty()) {
            Consola.info("Sin seguimientos.");
        }
        for (Seguimiento s : a.getSeguimiento()) {
            Consola.info(Consola.texto(s.getFecha()) + " | " + s.getDescripcion());
        }
    }

    private void registrarSolicitud() {
        Consola.subtitulo("Nueva solicitud de adopción");
        Animal animal = pedirPorId(animalDAO, "animal");
        if (animal == null) {
            return;
        }
        if (animal.getEstado() != EstadoAnimal.DISPONIBLE) {
            Consola.error("El animal no está disponible (estado: " + animal.getEstado() + ").");
            return;
        }
        Adoptante adoptante = pedirPorId(adoptanteDAO, "adoptante");
        if (adoptante == null) {
            return;
        }
        Adopcion a = new Adopcion();
        a.setAnimalId(animal.getId());
        a.setAdoptanteId(adoptante.getId());
        a.setFechaSolicitud(LocalDate.now());
        a.setEstado(EstadoAdopcion.PENDIENTE);
        a.setObservaciones(Consola.leerTextoOpcional("Observaciones", ""));

        dao.insertar(a);
        animalDAO.cambiarEstado(animal.getId(), EstadoAnimal.RESERVADO);
        Consola.ok("Solicitud registrada con el ID " + a.getId() + ". " + animal.getNombre() + " pasa a Reservado.");
    }

    /** Animal y adoptante no se cambian: para eso se da de baja y se crea otra solicitud. */
    private void modificar() {
        Adopcion a = pedirPorId(dao, "adopción");
        if (a == null) {
            return;
        }
        Consola.subtitulo("Modificar adopción (ENTER mantiene el valor actual)");
        a.setFechaSolicitud(Consola.leerFechaOpcional("Fecha de solicitud", a.getFechaSolicitud()));
        a.setObservaciones(Consola.leerTextoOpcional("Observaciones", a.getObservaciones()));

        dao.actualizar(a);
        Consola.ok("Adopción actualizada.");
    }

    private void resolver() {
        Adopcion a = pedirPorId(dao, "adopción");
        if (a == null) {
            return;
        }
        if (a.getEstado() != EstadoAdopcion.PENDIENTE) {
            Consola.error("La solicitud ya está resuelta (" + a.getEstado() + ").");
            return;
        }
        EstadoAdopcion[] opciones = {EstadoAdopcion.APROBADA, EstadoAdopcion.RECHAZADA};
        EstadoAdopcion nuevo = Consola.elegir("Resolución:", opciones, null);
        EstadoAnimal estadoAnimal = nuevo == EstadoAdopcion.APROBADA ? EstadoAnimal.ADOPTADO : EstadoAnimal.DISPONIBLE;

        dao.resolver(a.getId(), nuevo, LocalDate.now());
        animalDAO.cambiarEstado(a.getAnimalId(), estadoAnimal);
        Consola.ok("Solicitud " + nuevo + ". El animal pasa a " + estadoAnimal + ".");
    }

    private void anadirSeguimiento() {
        Adopcion a = pedirPorId(dao, "adopción");
        if (a == null) {
            return;
        }
        if (a.getEstado() != EstadoAdopcion.APROBADA) {
            Consola.error("Solo se puede hacer seguimiento de adopciones aprobadas.");
            return;
        }
        Seguimiento s = new Seguimiento(
                Consola.leerFecha("Fecha (dd/MM/yyyy): "),
                Consola.leerTexto("Descripción: "));
        dao.anadirSeguimiento(a.getId(), s);
        Consola.ok("Seguimiento añadido.");
    }

    /** Nadie referencia a una adopción, así que siempre se puede borrar. Si estaba pendiente, el animal se libera. */
    private void baja() {
        Adopcion a = pedirPorId(dao, "adopción");
        if (a == null) {
            return;
        }
        if (baja(dao, a.getId(), "la adopción " + a.getId(), false) && a.getEstado() == EstadoAdopcion.PENDIENTE) {
            animalDAO.cambiarEstado(a.getAnimalId(), EstadoAnimal.DISPONIBLE);
            Consola.info("El animal vuelve a estar Disponible.");
        }
    }

    private static String nombreAnimal(int id) {
        Animal animal = new AnimalDAO().buscarPorId(id);
        return animal == null ? "(eliminado)" : animal.getNombre();
    }

    private static String nombreAdoptante(int id) {
        Adoptante adoptante = new AdoptanteDAO().buscarPorId(id);
        return adoptante == null ? "(eliminado)" : adoptante.getNombreCompleto();
    }
}