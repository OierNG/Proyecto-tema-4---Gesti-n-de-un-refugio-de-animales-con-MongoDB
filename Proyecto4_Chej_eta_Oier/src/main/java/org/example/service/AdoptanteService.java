package org.example.service;

import org.example.exception.EntidadNoEncontradaException;
import org.example.exception.IntegridadException;
import org.example.exception.ValidacionException;
import org.example.model.Adoptante;
import org.example.repository.AdopcionRepository;
import org.example.repository.AdoptanteRepository;

import java.util.List;

public class AdoptanteService {

    public static final String PATRON_DNI = "(?i)[0-9]{8}[A-Z]|[XYZ][0-9]{7}[A-Z]";
    public static final String PATRON_EMAIL = "[^@\\s]+@[^@\\s]+\\.[^@\\s]+";
    public static final String PATRON_TELEFONO = "[0-9 +]{9,15}";
    public static final String[] SEXOS_PREFERIDOS = {"Macho", "Hembra", "Indiferente"};

    private final AdoptanteRepository repository;
    private final AdopcionRepository adopcionRepository;

    public AdoptanteService(AdoptanteRepository repository, AdopcionRepository adopcionRepository) {
        this.repository = repository;
        this.adopcionRepository = adopcionRepository;
    }

    public void alta(Adoptante adoptante) {
        validar(adoptante);
        repository.guardar(adoptante);
    }

    public void modificar(Adoptante adoptante) {
        validar(adoptante);
        buscarPorId(adoptante.getId());
        repository.actualizar(adoptante);
    }

    public void baja(int id, boolean definitiva) {
        buscarPorId(id);
        if (!definitiva) {
            repository.marcarEliminado(id);
            return;
        }
        long adopciones = adopcionRepository.contarPorAdoptante(id);
        if (adopciones > 0) {
            throw new IntegridadException("No se puede eliminar: el adoptante tiene " + adopciones
                    + " solicitud(es) o adopción(es). Solo se puede marcar como eliminado.");
        }
        repository.eliminarPorId(id);
    }

    public Adoptante buscarPorId(int id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe ningún adoptante con id " + id + "."));
    }

    public boolean existeDni(String dni, int idExcluido) {
        return repository.existeDni(dni.toUpperCase(), idExcluido);
    }

    public List<Adoptante> listarTodos() {
        return repository.listarTodos();
    }

    private void validar(Adoptante a) {
        if (a == null) throw new ValidacionException("El adoptante no puede ser nulo.");
        if (a.getDni() == null || !a.getDni().matches(PATRON_DNI)) throw new ValidacionException("DNI/NIE no válido.");
        a.setDni(a.getDni().toUpperCase());
        if (estaVacio(a.getNombre())) throw new ValidacionException("El nombre es obligatorio.");
        if (estaVacio(a.getApellidos())) throw new ValidacionException("Los apellidos son obligatorios.");
        if (a.getTelefono() == null || !a.getTelefono().matches(PATRON_TELEFONO)) throw new ValidacionException("Teléfono no válido.");
        if (estaVacio(a.getDireccion())) throw new ValidacionException("La dirección es obligatoria.");
        if (a.getEmail() == null || !a.getEmail().matches(PATRON_EMAIL)) throw new ValidacionException("Email no válido.");
        if (a.getFechaRegistro() == null) throw new ValidacionException("La fecha de registro es obligatoria.");
        if (a.getPreferencias() == null) throw new ValidacionException("Las preferencias son obligatorias.");
        if (repository.existeDni(a.getDni(), a.getId())) {
            throw new ValidacionException("Ya existe un adoptante con el DNI/NIE " + a.getDni() + ".");
        }
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
