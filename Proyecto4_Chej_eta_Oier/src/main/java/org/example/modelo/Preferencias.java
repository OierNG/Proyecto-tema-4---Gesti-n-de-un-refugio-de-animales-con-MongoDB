package org.example.modelo;

/** Preferencias de adopción: se guardan embebidas dentro del adoptante. */
public class Preferencias {

    private String especie;
    private String sexo;
    private int edadMaxima;

    public Preferencias() {
    }

    public Preferencias(String especie, String sexo, int edadMaxima) {
        this.especie = especie;
        this.sexo = sexo;
        this.edadMaxima = edadMaxima;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public int getEdadMaxima() {
        return edadMaxima;
    }

    public void setEdadMaxima(int edadMaxima) {
        this.edadMaxima = edadMaxima;
    }

    @Override
    public String toString() {
        return especie + ", " + sexo + ", hasta " + edadMaxima + " años";
    }
}
