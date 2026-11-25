package org.example.model;

public enum EstadoAnimal {
    DISPONIBLE("Disponible"),
    RESERVADO("Reservado"),
    ADOPTADO("Adoptado"),
    EN_TRATAMIENTO("En tratamiento");

    private final String texto;

    EstadoAnimal(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}
