package org.example.modelo;

public enum EstadoAdopcion {
    PENDIENTE("Pendiente"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada");

    private final String texto;

    EstadoAdopcion(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}