package org.example.ui;

import org.example.service.RefugioService;

public class MenuPrincipal extends MenuBase {

    private final RefugioService refugioService;
    private final MenuAnimales menuAnimales;
    private final MenuAdoptantes menuAdoptantes;
    private final MenuAdopciones menuAdopciones;
    private final MenuRefugio menuRefugio;

    public MenuPrincipal(RefugioService refugioService, MenuAnimales menuAnimales, MenuAdoptantes menuAdoptantes,
                         MenuAdopciones menuAdopciones, MenuRefugio menuRefugio) {
        super("MENÚ PRINCIPAL", "Salir");
        this.refugioService = refugioService;
        this.menuAnimales = menuAnimales;
        this.menuAdoptantes = menuAdoptantes;
        this.menuAdopciones = menuAdopciones;
        this.menuRefugio = menuRefugio;
    }

    @Override
    protected void cabecera() {
        System.out.println(refugioService.obtener());
        System.out.println();
    }

    @Override
    protected String[] opciones() {
        return new String[]{
                "Gestión de animales",
                "Gestión de adoptantes",
                "Gestión de adopciones",
                "Datos del refugio"
        };
    }

    @Override
    protected void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> menuAnimales.mostrar();
            case 2 -> menuAdoptantes.mostrar();
            case 3 -> menuAdopciones.mostrar();
            case 4 -> menuRefugio.mostrar();
        }
    }

    @Override
    protected boolean pausarDespuesDeOpcion() {
        return false;
    }
}
