package org.example.service;

import org.example.exception.EntidadNoEncontradaException;
import org.example.exception.ValidacionException;
import org.example.model.Adopcion;
import org.example.model.Adoptante;
import org.example.model.Animal;
import org.example.model.EstadoAdopcion;
import org.example.model.EstadoAnimal;
import org.example.model.Seguimiento;
import org.example.repository.AdopcionRepository;
import org.example.repository.AdoptanteRepository;
import org.example.repository.AnimalRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Cada cambio en una adopción mantiene coherente el estado del animal referenciado
public class AdopcionService {

    private final AdopcionRepository repository;
    private final AnimalRepository animalRepository;
    private final AdoptanteRepository adoptanteRepository;

    public AdopcionService(AdopcionRepository repository, AnimalRepository animalRepository,
                           AdoptanteRepository adoptanteRepository) {
        this.repository = repository;
        this.animalRepository = animalRepository;
        this.adoptanteRepository = adoptanteRepository;
    }

    // Solicitud nueva: el animal debe existir y estar disponible, y pasa a Reservado
    public Adopcion registrarSolicitud(int animalId, int adoptanteId, String observaciones) {
        Animal animal = animalRepository.buscarPorId(animalId)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe ningún animal con id " + animalId + "."));
        if (animal.getEstado() != EstadoAnimal.DISPONIBLE) {
            throw new ValidacionException("El animal no está disponible (estado: " + animal.getEstado() + ").");
        }
        adoptanteRepository.buscarPorId(adoptanteId)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe ningún adoptante con id " + adoptanteId + "."));

        Adopcion adopcion = new Adopcion(animalId, adoptanteId, LocalDate.now(), observaciones);
        repository.guardar(adopcion);
        animalRepository.cambiarEstado(animalId, EstadoAnimal.RESERVADO);
        return adopcion;
    }

    // El animal y el adoptante no se cambian: para eso se da de baja y se registra otra solicitud
    public void modificar(Adopcion adopcion) {
        buscarPorId(adopcion.getId());
        if (adopcion.getFechaSolicitud() == null) throw new ValidacionException("La fecha de solicitud es obligatoria.");
        repository.actualizar(adopcion);
    }

    // Aprobada -> el animal pasa a Adoptado; rechazada -> vuelve a Disponible
    public EstadoAnimal resolver(int id, EstadoAdopcion resolucion) {
        Adopcion adopcion = buscarPorId(id);
        if (adopcion.getEstado() != EstadoAdopcion.PENDIENTE) {
            throw new ValidacionException("La solicitud ya está resuelta (" + adopcion.getEstado() + ").");
        }
        if (resolucion == EstadoAdopcion.PENDIENTE) {
            throw new ValidacionException("La resolución debe ser Aprobada o Rechazada.");
        }
        adopcion.setEstado(resolucion);
        adopcion.setFechaResolucion(LocalDate.now());
        repository.actualizar(adopcion);

        EstadoAnimal estadoAnimal = resolucion == EstadoAdopcion.APROBADA ? EstadoAnimal.ADOPTADO : EstadoAnimal.DISPONIBLE;
        animalRepository.cambiarEstado(adopcion.getAnimalId(), estadoAnimal);
        return estadoAnimal;
    }

    public void anadirSeguimiento(int id, Seguimiento seguimiento) {
        Adopcion adopcion = buscarPorId(id);
        if (adopcion.getEstado() != EstadoAdopcion.APROBADA) {
            throw new ValidacionException("Solo se puede hacer seguimiento de adopciones aprobadas.");
        }
        if (seguimiento.getFecha() == null) throw new ValidacionException("La fecha es obligatoria.");
        if (seguimiento.getDescripcion() == null || seguimiento.getDescripcion().isBlank()) {
            throw new ValidacionException("La descripción es obligatoria.");
        }
        repository.anadirSeguimiento(id, seguimiento);
    }

    // Ninguna colección referencia a una adopción, así que siempre se puede borrar. Si estaba pendiente, el animal se libera
    public void baja(int id, boolean definitiva) {
        Adopcion adopcion = buscarPorId(id);
        if (definitiva) {
            repository.eliminarPorId(id);
        } else {
            repository.marcarEliminado(id);
        }
        if (adopcion.getEstado() == EstadoAdopcion.PENDIENTE) {
            animalRepository.cambiarEstado(adopcion.getAnimalId(), EstadoAnimal.DISPONIBLE);
        }
    }

    public Adopcion buscarPorId(int id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe ninguna adopción con id " + id + "."));
    }

    public List<Adopcion> listarTodos() {
        return repository.listarTodos();
    }

    public List<Adopcion> buscarPorAdoptante(int idAdoptante) {
        return repository.buscarPorAdoptante(idAdoptante);
    }

    // Vacío si el animal o el adoptante referenciado se ha marcado como eliminado
    public Optional<Animal> animalDe(Adopcion adopcion) {
        return animalRepository.buscarPorId(adopcion.getAnimalId());
    }

    public Optional<Adoptante> adoptanteDe(Adopcion adopcion) {
        return adoptanteRepository.buscarPorId(adopcion.getAdoptanteId());
    }
}
