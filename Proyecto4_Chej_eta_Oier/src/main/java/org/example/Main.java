package org.example;

import org.example.repository.AdopcionRepository;
import org.example.repository.AdoptanteRepository;
import org.example.repository.AnimalRepository;
import org.example.repository.RefugioRepository;
import org.example.service.AdopcionService;
import org.example.service.AdoptanteService;
import org.example.service.AnimalService;
import org.example.service.ExportacionService;
import org.example.service.RefugioService;
import org.example.ui.MenuAdopciones;
import org.example.ui.MenuAdoptantes;
import org.example.ui.MenuAnimales;
import org.example.ui.MenuPrincipal;
import org.example.ui.MenuRefugio;
import org.example.util.MongoUtil;

public class Main {

    public static void main(String[] args) {
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║     REFUGIO DE ANIMALES - MONGODB    ║
                ╚══════════════════════════════════════╝""");
        System.out.println("Conectando con MongoDB...");

        try {
            MongoUtil.inicializar();
        } catch (Exception e) {
            System.out.println("ERROR: no se pudo conectar con MongoDB en localhost:27017. ¿Está arrancado el servidor?");
            MongoUtil.cerrar();
            return;
        }

        try {
            // Construcción de dependencias: repository → service → menú
            AnimalRepository animalRepository = new AnimalRepository();
            AdoptanteRepository adoptanteRepository = new AdoptanteRepository();
            AdopcionRepository adopcionRepository = new AdopcionRepository();

            AnimalService animalService = new AnimalService(animalRepository, adopcionRepository);
            AdoptanteService adoptanteService = new AdoptanteService(adoptanteRepository, adopcionRepository);
            AdopcionService adopcionService = new AdopcionService(adopcionRepository, animalRepository, adoptanteRepository);
            RefugioService refugioService = new RefugioService(new RefugioRepository());
            ExportacionService exportacionService = new ExportacionService();

            refugioService.crearSiNoExiste();

            MenuPrincipal menuPrincipal = new MenuPrincipal(
                    refugioService,
                    new MenuAnimales(animalService, exportacionService),
                    new MenuAdoptantes(adoptanteService, adopcionService, exportacionService),
                    new MenuAdopciones(adopcionService, exportacionService),
                    new MenuRefugio(refugioService)
            );

            menuPrincipal.mostrar();
            System.out.println("¡Hasta pronto!");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        } finally {
            MongoUtil.cerrar();
        }
    }
}
