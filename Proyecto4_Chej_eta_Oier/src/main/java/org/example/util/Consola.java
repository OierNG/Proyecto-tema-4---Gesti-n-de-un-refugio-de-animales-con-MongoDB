package org.example.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Entrada/salida por consola. Todas las lecturas validan, así ningún dato erróneo lanza excepción. */
public final class Consola {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Consola() {
    }

    public static void titulo(String texto) {
        System.out.println();
        System.out.println("==========================================================");
        System.out.println("  " + texto);
        System.out.println("==========================================================");
    }

    public static void subtitulo(String texto) {
        System.out.println();
        System.out.println("--- " + texto + " ---");
    }

    public static void info(String texto) {
        System.out.println(texto);
    }

    public static void ok(String texto) {
        System.out.println("[OK] " + texto);
    }

    public static void error(String texto) {
        System.out.println("[ERROR] " + texto);
    }

    public static void pausa() {
        System.out.print("Pulsa ENTER para continuar...");
        if (SCANNER.hasNextLine()) {
            SCANNER.nextLine();
        }
    }

    public static String leerTexto(String mensaje) {
        while (true) {
            String valor = leerLinea(mensaje);
            if (!valor.isEmpty()) {
                return valor;
            }
            error("El valor no puede estar vacío.");
        }
    }

    /** Texto obligatorio que además debe cumplir una expresión regular (DNI, email...). */
    public static String leerTexto(String mensaje, String formato, String errorFormato) {
        while (true) {
            String valor = leerTexto(mensaje);
            if (valor.matches(formato)) {
                return valor;
            }
            error(errorFormato);
        }
    }

    /** Los métodos "Opcional" mantienen el valor actual si el usuario pulsa ENTER. */
    public static String leerTextoOpcional(String mensaje, String valorActual) {
        String valor = leerLinea(mensaje + " [" + (valorActual == null ? "" : valorActual) + "]: ");
        return valor.isEmpty() ? valorActual : valor;
    }

    public static String leerTextoOpcional(String mensaje, String valorActual, String formato, String errorFormato) {
        while (true) {
            String valor = leerTextoOpcional(mensaje, valorActual);
            if (valor != null && valor.matches(formato)) {
                return valor;
            }
            error(errorFormato);
        }
    }

    public static int leerEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            String valor = leerLinea(mensaje);
            try {
                int numero = Integer.parseInt(valor);
                if (numero < minimo || numero > maximo) {
                    error("El número debe estar entre " + minimo + " y " + maximo + ".");
                    continue;
                }
                return numero;
            } catch (NumberFormatException e) {
                error("'" + valor + "' no es un número válido.");
            }
        }
    }

    public static int leerEnteroOpcional(String mensaje, int valorActual, int minimo, int maximo) {
        while (true) {
            String valor = leerLinea(mensaje + " [" + valorActual + "]: ");
            if (valor.isEmpty()) {
                return valorActual;
            }
            try {
                int numero = Integer.parseInt(valor);
                if (numero >= minimo && numero <= maximo) {
                    return numero;
                }
                error("El número debe estar entre " + minimo + " y " + maximo + ".");
            } catch (NumberFormatException e) {
                error("'" + valor + "' no es un número válido.");
            }
        }
    }

    /** Muestra las opciones numeradas y devuelve la elegida. Si actual != null, 0 la mantiene. */
    public static <E> E elegir(String mensaje, E[] opciones, E actual) {
        info(mensaje);
        for (int i = 0; i < opciones.length; i++) {
            info("  " + (i + 1) + ". " + opciones[i]);
        }
        if (actual != null) {
            info("  0. Mantener [" + actual + "]");
        }
        int opcion = leerEntero("Opción: ", actual == null ? 1 : 0, opciones.length);
        return opcion == 0 ? actual : opciones[opcion - 1];
    }

    public static LocalDate leerFecha(String mensaje) {
        while (true) {
            String valor = leerLinea(mensaje);
            try {
                return LocalDate.parse(valor, FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                error("Fecha no válida. Usa el formato dd/MM/yyyy (ejemplo: 15/03/2024).");
            }
        }
    }

    public static LocalDate leerFechaOpcional(String mensaje, LocalDate fechaActual) {
        while (true) {
            String valor = leerLinea(mensaje + " [" + Consola.texto(fechaActual) + "]: ");
            if (valor.isEmpty()) {
                return fechaActual;
            }
            try {
                return LocalDate.parse(valor, FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                error("Fecha no válida. Usa el formato dd/MM/yyyy (ejemplo: 15/03/2024).");
            }
        }
    }

    public static boolean confirmar(String mensaje) {
        while (true) {
            String valor = leerLinea(mensaje + " (s/n): ").toLowerCase();
            if (valor.equals("s") || valor.equals("si") || valor.equals("sí")) {
                return true;
            }
            if (valor.equals("n") || valor.equals("no")) {
                return false;
            }
            error("Responde 's' o 'n'.");
        }
    }

    /** Fecha a texto dd/MM/yyyy, o "-" si es nula. */
    public static String texto(LocalDate fecha) {
        return fecha == null ? "-" : fecha.format(FORMATO_FECHA);
    }

    private static String leerLinea(String mensaje) {
        System.out.print(mensaje);
        if (!SCANNER.hasNextLine()) {
            System.out.println();
            System.out.println();
            System.out.println("Entrada finalizada. Cerrando la aplicación...");
            System.exit(0);
        }
        return SCANNER.nextLine().trim();
    }
}