package org.example.model;

import org.example.util.Consola;

import java.time.LocalDate;

// Se guarda embebido en el array seguimiento de la adopción
public class Seguimiento {

    private LocalDate fecha;
    private String descripcion;

    public Seguimiento() {
    }

    public Seguimiento(LocalDate fecha, String descripcion) {
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return Consola.formatear(fecha) + " | " + descripcion;
    }
}
