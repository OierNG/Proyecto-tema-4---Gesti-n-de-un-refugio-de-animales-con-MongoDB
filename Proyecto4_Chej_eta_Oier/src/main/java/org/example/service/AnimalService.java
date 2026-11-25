package org.example.service;

import org.example.exception.EntidadNoEncontradaException;
import org.example.exception.IntegridadException;
import org.example.exception.ValidacionException;
import org.example.model.Animal;
import org.example.model.RegistroMedico;
import org.example.repository.AdopcionRepository;
import org.example.repository.AnimalRepository;

import java.util.List;

// Reglas de negocio de animales. El menú nunca habla directamente con el repositorio
public class AnimalService {

    public static final String[] SEXOS = {"Macho", "Hembra"};
    public static final int EDAD_MAXIMA = 40;

    private final AnimalRepository repository;
    private final AdopcionRepository adopcionRepository;

    public AnimalService(AnimalRepository repository, AdopcionRepository adopcionRepository) {
        this.repository = repository;
        this.adopcionRepository = adopcionRepository;
    }

    public void alta(Animal animal) {
        validar(animal);
        repository.guardar(animal);
    }

    public void modificar(Animal animal) {
        validar(animal);
        buscarPorId(animal.getId());
        repository.actualizar(animal);
    }

    // Baja definitiva solo si ninguna adopción apunta al animal; si no, solo se puede marcar como eliminado
    public void baja(int id, boolean definitiva) {
        buscarPorId(id);
        if (!definitiva) {
            repository.marcarEliminado(id);
            return;
        }
        long adopciones = adopcionRepository.contarPorAnimal(id);
        if (adopciones > 0) {
            throw new IntegridadException("No se puede eliminar: el animal tiene " + adopciones
                    + " adopción(es) asociada(s). Solo se puede marcar como eliminado.");
        }
        repository.eliminarPorId(id);
    }

    public void anadirRegistroMedico(int id, RegistroMedico registro) {
        buscarPorId(id);
        if (registro.getFecha() == null) throw new ValidacionException("La fecha es obligatoria.");
        if (estaVacio(registro.getTipo())) throw new ValidacionException("El tipo es obligatorio.");
        if (estaVacio(registro.getDescripcion())) throw new ValidacionException("La descripción es obligatoria.");
        if (estaVacio(registro.getVeterinario())) throw new ValidacionException("El veterinario es obligatorio.");
        repository.anadirRegistroMedico(id, registro);
    }

    public Animal buscarPorId(int id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe ningún animal con id " + id + "."));
    }

    public List<Animal> listarTodos() {
        return repository.listarTodos();
    }

    // Segunda capa de validación: el servicio no confía en que quien lo llame haya validado
    private void validar(Animal a) {
        if (a == null) throw new ValidacionException("El animal no puede ser nulo.");
        if (estaVacio(a.getNombre())) throw new ValidacionException("El nombre es obligatorio.");
        if (estaVacio(a.getEspecie())) throw new ValidacionException("La especie es obligatoria.");
        if (estaVacio(a.getRaza())) throw new ValidacionException("La raza es obligatoria.");
        if (a.getEdad() < 0 || a.getEdad() > EDAD_MAXIMA) throw new ValidacionException("La edad debe estar entre 0 y " + EDAD_MAXIMA + ".");
        if (estaVacio(a.getSexo())) throw new ValidacionException("El sexo es obligatorio.");
        if (a.getFechaIngreso() == null) throw new ValidacionException("La fecha de ingreso es obligatoria.");
        if (a.getEstado() == null) throw new ValidacionException("El estado es obligatorio.");
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
