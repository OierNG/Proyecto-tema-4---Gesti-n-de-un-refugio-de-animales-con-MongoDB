package org.example.model;

import org.example.util.Consola;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// El animal y el adoptante se guardan por referencia (su id), no embebidos
public class Adopcion {

    private int id;
    private int animalId;
    private int adoptanteId;
    private LocalDate fechaSolicitud;
    private LocalDate fechaResolucion;
    private EstadoAdopcion estado;
    private String observaciones;
    private List<Seguimiento> seguimiento = new ArrayList<>();
    private boolean eliminado;

    public Adopcion() {
    }

    public Adopcion(int animalId, int adoptanteId, LocalDate fechaSolicitud, String observaciones) {
        this.animalId = animalId;
        this.adoptanteId = adoptanteId;
        this.fechaSolicitud = fechaSolicitud;
        this.observaciones = observaciones;
        this.estado = EstadoAdopcion.PENDIENTE;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAnimalId() { return animalId; }
    public void setAnimalId(int animalId) { this.animalId = animalId; }

    public int getAdoptanteId() { return adoptanteId; }
    public void setAdoptanteId(int adoptanteId) { this.adoptanteId = adoptanteId; }

    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDate fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public LocalDate getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDate fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public EstadoAdopcion getEstado() { return estado; }
    public void setEstado(EstadoAdopcion estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public List<Seguimiento> getSeguimiento() { return seguimiento; }
    public void setSeguimiento(List<Seguimiento> seguimiento) { this.seguimiento = seguimiento; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }

    @Override
    public String toString() {
        return String.format("[%d] Animal %d - Adoptante %d - Solicitud: %s - Resolución: %s - %s",
                id, animalId, adoptanteId, Consola.formatear(fechaSolicitud), Consola.formatear(fechaResolucion), estado);
    }
}
