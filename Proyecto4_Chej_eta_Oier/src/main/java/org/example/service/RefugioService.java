package org.example.service;

import org.example.exception.EntidadNoEncontradaException;
import org.example.exception.ValidacionException;
import org.example.model.Refugio;
import org.example.repository.RefugioRepository;

import java.time.LocalDate;

public class RefugioService {

    private final RefugioRepository repository;

    public RefugioService(RefugioRepository repository) {
        this.repository = repository;
    }

    // En el primer arranque se crea el documento del refugio para que el menú principal tenga qué mostrar
    public void crearSiNoExiste() {
        if (repository.obtener().isEmpty()) {
            repository.guardar(new Refugio("Refugio de Animales", "G12345678", "Calle Mayor 1, 20001 Donostia",
                    "943000000", "info@refugio.org", LocalDate.of(2015, 1, 1)));
        }
    }

    public Refugio obtener() {
        return repository.obtener()
                .orElseThrow(() -> new EntidadNoEncontradaException("Todavía no hay datos del refugio."));
    }

    public void modificar(Refugio r) {
        if (r.getNombre() == null || r.getNombre().isBlank()) throw new ValidacionException("El nombre es obligatorio.");
        if (r.getCif() == null || r.getCif().isBlank()) throw new ValidacionException("El CIF es obligatorio.");
        if (r.getFechaApertura() == null) throw new ValidacionException("La fecha de apertura es obligatoria.");
        repository.guardar(r);
    }
}
