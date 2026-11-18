package org.example.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Utilidades de entrada/salida por consola.
 *
 * Centraliza la lectura de datos para que todos los menús validen igual:
 * ningún dato incorrecto del usuario debe provocar una excepción.
 */
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

    /** Espera a que el usuario pulse ENTER. */
    public static void pausa() {
        System.out.print("Pulsa ENTER para continuar...");
        if (SCANNER.hasNextLine()) {
            SCANNER.nextLine();
        }
    }

    /** Lee un texto obligatorio (no admite vacío). */
    public static String leerTexto(String mensaje) {
        while (true) {
            String valor = leerLinea(mensaje);
            if (!valor.isEmpty()) {
                return valor;
            }
            error("El valor no puede estar vacío.");
        }
    }

    /**
     * Lee un texto opcional. Si el usuario pulsa ENTER se mantiene el valor actual.
     */
    public static String leerTextoOpcional(String mensaje, String valorActual) {
        String valor = leerLinea(mensaje + " [" + (valorActual == null ? "" : valorActual) + "]: ");
        return valor.isEmpty() ? valorActual : valor;
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

    /** Lee una fecha con formato dd/MM/yyyy. */
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

    /**
     * Lee una fecha opcional: si el usuario pulsa ENTER se mantiene la fecha actual.
     */
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

    /** Pregunta de sí/no. Devuelve true si la respuesta es 's'. */
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

    /** Convierte una fecha a texto dd/MM/yyyy (o "-" si es nula). */
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