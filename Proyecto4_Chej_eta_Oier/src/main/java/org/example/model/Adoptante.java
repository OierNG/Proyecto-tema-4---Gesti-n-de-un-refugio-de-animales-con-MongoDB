package org.example.model;

import org.example.util.Consola;

import java.time.LocalDate;

public class Adoptante {

    private int id;
    private String dni;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String email;
    private LocalDate fechaRegistro;
    private Preferencias preferencias = new Preferencias();
    private boolean eliminado;

    public Adoptante() {
    }

    public Adoptante(String dni, String nombre, String apellidos, String telefono, String direccion,
                     String email, LocalDate fechaRegistro, Preferencias preferencias) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.direccion = direccion;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
        this.preferencias = preferencias;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Preferencias getPreferencias() { return preferencias; }
    public void setPreferencias(Preferencias preferencias) { this.preferencias = preferencias; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }

    @Override
    public String toString() {
        return String.format("[%d] %s %s (%s) - Tel: %s - %s - Registro: %s",
                id, nombre, apellidos, dni, telefono, email, Consola.formatear(fechaRegistro));
    }
}
