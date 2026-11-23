package org.example.modelo;

import java.time.LocalDate;

public class RegistroMedico {

    private LocalDate fecha;
    private String tipo;
    private String descripcion;
    private String veterinario;

    public RegistroMedico() {
    }

    public RegistroMedico(LocalDate fecha, String tipo, String descripcion, String veterinario) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.veterinario = veterinario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }
}
