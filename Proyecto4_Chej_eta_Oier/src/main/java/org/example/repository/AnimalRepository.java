package org.example.repository;

import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.example.model.Animal;
import org.example.model.EstadoAnimal;
import org.example.model.RegistroMedico;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

public class AnimalRepository extends RepositorioBase<Animal> {

    public AnimalRepository() {
        super("animales");
    }

    @Override
    public void guardar(Animal animal) {
        animal.setId(siguienteId());
        super.guardar(animal);
    }

    // $push: añade el registro al array embebido sin reescribir el animal entero
    public void anadirRegistroMedico(int idAnimal, RegistroMedico registro) {
        ejecutar(coleccion -> coleccion.updateOne(eq("_id", idAnimal),
                Updates.push("historialMedico", aDocumento(registro))));
    }

    public void cambiarEstado(int idAnimal, EstadoAnimal estado) {
        ejecutar(coleccion -> coleccion.updateOne(eq("_id", idAnimal), Updates.set("estado", estado.name())));
    }

    @Override
    protected Document aDocumento(Animal animal) {
        return new Document("_id", animal.getId())
                .append("nombre", animal.getNombre())
                .append("especie", animal.getEspecie())
                .append("raza", animal.getRaza())
                .append("edad", animal.getEdad())
                .append("sexo", animal.getSexo())
                .append("descripcion", animal.getDescripcion())
                .append("fechaIngreso", aDate(animal.getFechaIngreso()))
                .append("estado", animal.getEstado().name())
                .append("historialMedico", animal.getHistorialMedico().stream().map(this::aDocumento).toList())
                .append("eliminado", animal.isEliminado());
    }

    @Override
    protected Animal aEntidad(Document documento) {
        Animal animal = new Animal(
                documento.getString("nombre"),
                documento.getString("especie"),
                documento.getString("raza"),
                documento.getInteger("edad", 0),
                documento.getString("sexo"),
                documento.getString("descripcion"),
                aLocalDate(documento.getDate("fechaIngreso")),
                EstadoAnimal.valueOf(documento.getString("estado")));
        animal.setId(documento.getInteger("_id"));
        animal.setEliminado(documento.getBoolean("eliminado", false));
        for (Document registro : documento.getList("historialMedico", Document.class, new ArrayList<>())) {
            animal.getHistorialMedico().add(new RegistroMedico(
                    aLocalDate(registro.getDate("fecha")),
                    registro.getString("tipo"),
                    registro.getString("descripcion"),
                    registro.getString("veterinario")));
        }
        return animal;
    }

    private Document aDocumento(RegistroMedico registro) {
        return new Document("fecha", aDate(registro.getFecha()))
                .append("tipo", registro.getTipo())
                .append("descripcion", registro.getDescripcion())
                .append("veterinario", registro.getVeterinario());
    }
}
