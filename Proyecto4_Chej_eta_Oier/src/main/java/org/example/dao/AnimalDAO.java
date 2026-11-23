package org.example.dao;

import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.example.modelo.Animal;
import org.example.modelo.EstadoAnimal;
import org.example.modelo.RegistroMedico;
import org.example.util.Fechas;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class AnimalDAO extends BaseDAO<Animal> {

    public AnimalDAO() {
        super("animales");
    }

    @Override
    public void insertar(Animal animal) {
        animal.setId(siguienteId());
        super.insertar(animal);
    }

    public void anadirRegistroMedico(int idAnimal, RegistroMedico registro) {
        coleccion().updateOne(eq("_id", idAnimal), Updates.push("historialMedico", aDocumento(registro)));
    }

    public void cambiarEstado(int idAnimal, EstadoAnimal estado) {
        coleccion().updateOne(eq("_id", idAnimal), Updates.set("estado", estado.name()));
    }

    public boolean tieneAdopciones(int idAnimal) {
        return estaReferenciado("adopciones", "animalId", idAnimal);
    }

    @Override
    protected Document aDocumento(Animal animal) {
        List<Document> historial = new ArrayList<>();
        for (RegistroMedico registro : animal.getHistorialMedico()) {
            historial.add(aDocumento(registro));
        }
        return new Document("_id", animal.getId())
                .append("nombre", animal.getNombre())
                .append("especie", animal.getEspecie())
                .append("raza", animal.getRaza())
                .append("edad", animal.getEdad())
                .append("sexo", animal.getSexo())
                .append("descripcion", animal.getDescripcion())
                .append("fechaIngreso", Fechas.aDate(animal.getFechaIngreso()))
                .append("estado", animal.getEstado().name())
                .append("historialMedico", historial)
                .append("eliminado", animal.isEliminado());
    }

    @Override
    protected Animal aObjeto(Document documento) {
        Animal animal = new Animal();
        animal.setId(documento.getInteger("_id"));
        animal.setNombre(documento.getString("nombre"));
        animal.setEspecie(documento.getString("especie"));
        animal.setRaza(documento.getString("raza"));
        animal.setEdad(documento.getInteger("edad", 0));
        animal.setSexo(documento.getString("sexo"));
        animal.setDescripcion(documento.getString("descripcion"));
        animal.setFechaIngreso(Fechas.aLocalDate(documento.getDate("fechaIngreso")));
        String estado = documento.getString("estado");
        animal.setEstado(estado == null ? EstadoAnimal.DISPONIBLE : EstadoAnimal.valueOf(estado));
        animal.setEliminado(documento.getBoolean("eliminado", false));

        for (Document registro : documento.getList("historialMedico", Document.class, new ArrayList<>())) {
            animal.getHistorialMedico().add(new RegistroMedico(
                    Fechas.aLocalDate(registro.getDate("fecha")),
                    registro.getString("tipo"),
                    registro.getString("descripcion"),
                    registro.getString("veterinario")));
        }
        return animal;
    }

    private Document aDocumento(RegistroMedico registro) {
        return new Document("fecha", Fechas.aDate(registro.getFecha()))
                .append("tipo", registro.getTipo())
                .append("descripcion", registro.getDescripcion())
                .append("veterinario", registro.getVeterinario());
    }
}
