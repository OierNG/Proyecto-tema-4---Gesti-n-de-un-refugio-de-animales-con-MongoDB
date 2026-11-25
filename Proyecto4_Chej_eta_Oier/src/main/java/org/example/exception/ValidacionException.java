package org.example.exception;

// Datos que no cumplen las reglas (formato, rangos, campos obligatorios...)
public class ValidacionException extends RefugioException {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
