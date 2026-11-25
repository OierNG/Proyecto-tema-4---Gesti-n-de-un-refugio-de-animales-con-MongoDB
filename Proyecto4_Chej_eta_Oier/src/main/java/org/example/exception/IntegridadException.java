package org.example.exception;

// Operación que rompería las referencias entre colecciones (ej: borrar un animal con adopciones)
public class IntegridadException extends RefugioException {

    public IntegridadException(String mensaje) {
        super(mensaje);
    }
}
