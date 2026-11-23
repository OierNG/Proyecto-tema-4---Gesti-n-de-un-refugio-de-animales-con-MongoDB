package org.example.vista;

import org.example.dao.AdoptanteDAO;
import org.example.modelo.Adoptante;
import org.example.modelo.Preferencias;
import org.example.util.Consola;

import java.time.LocalDate;
import java.util.List;

public final class MenuAdoptantes extends Menu {

    private static final String FORMATO_DNI = "(?i)[0-9]{8}[A-Z]|[XYZ][0-9]{7}[A-Z]";
    private static final String ERROR_DNI = "DNI/NIE no válido (ejemplos: 12345678A, X1234567B).";
    private static final String FORMATO_EMAIL = "[^@\\s]+@[^@\\s]+\\.[^@\\s]+";
    private static final String ERROR_EMAIL = "Email no válido (ejemplo: nombre@correo.com).";
    private static final String[] SEXOS = {"Macho", "Hembra", "Indiferente"};

    private final AdoptanteDAO dao = new AdoptanteDAO();

    public MenuAdoptantes() {
        super("GESTIÓN DE ADOPTANTES",
                "Listar adoptantes",
                "Ver ficha",
                "Alta de adoptante",
                "Modificar adoptante",
                "Baja de adoptante");
    }

    @Override
    protected void ejecutar(int opcion) {
        switch (opcion) {
            case 1 -> listar();
            case 2 -> verFicha();
            case 3 -> alta();
            case 4 -> modificar();
            case 5 -> baja();
        }
    }

    private void listar() {
        List<Adoptante> adoptantes = dao.listar();
        Consola.subtitulo("Adoptantes registrados (" + adoptantes.size() + ")");
        if (adoptantes.isEmpty()) {
            Consola.info("No hay adoptantes registrados.");
            return;
        }
        Consola.info(String.format("%-4s %-10s %-28s %-12s %-26s %s",
                "ID", "DNI/NIE", "NOMBRE", "TELÉFONO", "EMAIL", "REGISTRO"));
        for (Adoptante a : adoptantes) {
            Consola.info(String.format("%-4d %-10s %-28s %-12s %-26s %s",
                    a.getId(), a.getDni(), a.getNombreCompleto(), a.getTelefono(),
                    a.getEmail(), Consola.texto(a.getFechaRegistro())));
        }
    }

    private void verFicha() {
        Adoptante a = pedirPorId(dao, "adoptante");
        if (a == null) {
            return;
        }
        Consola.subtitulo("Ficha del adoptante " + a.getId());
        Consola.info("DNI/NIE......: " + a.getDni());
        Consola.info("Nombre.......: " + a.getNombreCompleto());
        Consola.info("Teléfono.....: " + a.getTelefono());
        Consola.info("Dirección....: " + a.getDireccion());
        Consola.info("Email........: " + a.getEmail());
        Consola.info("Registro.....: " + Consola.texto(a.getFechaRegistro()));
        Consola.info("Preferencias.: " + a.getPreferencias());
    }

    private void alta() {
        Consola.subtitulo("Alta de adoptante");
        String dni = Consola.leerTexto("DNI/NIE: ", FORMATO_DNI, ERROR_DNI).toUpperCase();
        if (dao.existeDni(dni, 0)) {
            Consola.error("Ya existe un adoptante con el DNI/NIE " + dni + ".");
            return;
        }
        Adoptante a = new Adoptante();
        a.setDni(dni);
        a.setNombre(Consola.leerTexto("Nombre: "));
        a.setApellidos(Consola.leerTexto("Apellidos: "));
        a.setTelefono(Consola.leerTexto("Teléfono: "));
        a.setDireccion(Consola.leerTexto("Dirección: "));
        a.setEmail(Consola.leerTexto("Email: ", FORMATO_EMAIL, ERROR_EMAIL));
        a.setFechaRegistro(LocalDate.now());

        Consola.info("Preferencias de adopción:");
        a.setPreferencias(new Preferencias(
                Consola.leerTexto("  Especie (perro, gato, cualquiera...): "),
                Consola.elegir("  Sexo:", SEXOS, null),
                Consola.leerEntero("  Edad máxima (años): ", 0, 40)));

        dao.insertar(a);
        Consola.ok("Adoptante registrado con el ID " + a.getId() + ".");
    }

    private void modificar() {
        Adoptante a = pedirPorId(dao, "adoptante");
        if (a == null) {
            return;
        }
        Consola.subtitulo("Modificar adoptante (ENTER mantiene el valor actual)");
        String dni = Consola.leerTextoOpcional("DNI/NIE", a.getDni(), FORMATO_DNI, ERROR_DNI).toUpperCase();
        if (dao.existeDni(dni, a.getId())) {
            Consola.error("Ya existe otro adoptante con el DNI/NIE " + dni + ".");
            return;
        }
        a.setDni(dni);
        a.setNombre(Consola.leerTextoOpcional("Nombre", a.getNombre()));
        a.setApellidos(Consola.leerTextoOpcional("Apellidos", a.getApellidos()));
        a.setTelefono(Consola.leerTextoOpcional("Teléfono", a.getTelefono()));
        a.setDireccion(Consola.leerTextoOpcional("Dirección", a.getDireccion()));
        a.setEmail(Consola.leerTextoOpcional("Email", a.getEmail(), FORMATO_EMAIL, ERROR_EMAIL));

        Preferencias p = a.getPreferencias();
        Consola.info("Preferencias de adopción:");
        p.setEspecie(Consola.leerTextoOpcional("  Especie", p.getEspecie()));
        p.setSexo(Consola.elegir("  Sexo:", SEXOS, p.getSexo()));
        p.setEdadMaxima(Consola.leerEnteroOpcional("  Edad máxima", p.getEdadMaxima(), 0, 40));

        dao.actualizar(a);
        Consola.ok("Adoptante actualizado.");
    }

    private void baja() {
        Adoptante a = pedirPorId(dao, "adoptante");
        if (a != null) {
            baja(dao, a.getId(), a.getNombreCompleto(), dao.tieneAdopciones(a.getId()));
        }
    }
}
