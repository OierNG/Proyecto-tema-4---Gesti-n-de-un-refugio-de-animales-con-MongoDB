package org.example.exception;

// Excepción base de la aplicación: el menú captura este tipo y muestra el mensaje
public class RefugioException extends RuntimeException {

    public RefugioException(String mensaje) {
        super(mensaje);
    }

    public RefugioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
