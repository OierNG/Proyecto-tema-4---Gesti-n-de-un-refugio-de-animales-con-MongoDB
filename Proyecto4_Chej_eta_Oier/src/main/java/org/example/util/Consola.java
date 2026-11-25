package org.example.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.NoSuchElementException;
import java.util.Scanner;

// Lectura de datos por teclado con validación: repite la pregunta hasta que el dato sea correcto
public class Consola {

    private static final Scanner scanner = new Scanner(System.in);

    // STRICT para que se rechacen fechas imposibles como 31/02
    public static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Consola() {
    }

    // Punto único de lectura: si se cierra la entrada se sale de forma controlada
    private static String leerLinea(String mensaje) {
        System.out.print(mensaje);
        System.out.flush();
        try {
            return scanner.nextLine().trim();
        } catch (NoSuchElementException | IllegalStateException e) {
            System.out.println("\nEntrada cerrada. Saliendo de la aplicación...");
            MongoUtil.cerrar();
            System.exit(0);
            return "";
        }
    }

    private static void error(String mensaje) {
        System.out.println("  ✗ " + mensaje);
    }

    public static String leerTexto(String mensaje, int longitudMaxima) {
        while (true) {
            String texto = leerLinea(mensaje);
            if (texto.isEmpty()) {
                error("El campo no puede estar vacío.");
            } else if (texto.length() > longitudMaxima) {
                error("Máximo " + longitudMaxima + " caracteres.");
            } else {
                return texto;
            }
        }
    }

    // Para modificaciones: si el usuario pulsa Enter se mantiene el valor actual
    public static String leerTextoOpcional(String mensaje, String valorActual, int longitudMaxima) {
        while (true) {
            String texto = leerLinea(mensaje + " [" + valorActual + "]: ");
            if (texto.isEmpty()) {
                return valorActual;
            } else if (texto.length() > longitudMaxima) {
                error("Máximo " + longitudMaxima + " caracteres.");
            } else {
                return texto;
            }
        }
    }

    // Texto que debe cumplir una expresión regular (DNI/NIE, email, teléfono...)
    public static String leerConFormato(String mensaje, String patron, String mensajeError) {
        while (true) {
            String texto = leerLinea(mensaje);
            if (texto.matches(patron)) {
                return texto;
            }
            error(mensajeError);
        }
    }

    public static String leerConFormatoOpcional(String mensaje, String valorActual, String patron, String mensajeError) {
        while (true) {
            String texto = leerLinea(mensaje + " [" + valorActual + "]: ");
            if (texto.isEmpty()) {
                return valorActual;
            }
            if (texto.matches(patron)) {
                return texto;
            }
            error(mensajeError);
        }
    }

    public static int leerEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            String texto = leerLinea(mensaje);
            if (texto.isEmpty()) {
                error("No has escrito nada. Introduce un número entre " + minimo + " y " + maximo + ".");
                continue;
            }
            try {
                int numero = Integer.parseInt(texto);
                if (numero >= minimo && numero <= maximo) {
                    return numero;
                }
                error("Debe estar entre " + minimo + " y " + maximo + ".");
            } catch (NumberFormatException e) {
                error("'" + texto + "' no es un número entero.");
            }
        }
    }

    public static int leerEnteroOpcional(String mensaje, int valorActual, int minimo, int maximo) {
        while (true) {
            String texto = leerLinea(mensaje + " [" + valorActual + "]: ");
            if (texto.isEmpty()) {
                return valorActual;
            }
            try {
                int numero = Integer.parseInt(texto);
                if (numero >= minimo && numero <= maximo) {
                    return numero;
                }
                error("Debe estar entre " + minimo + " y " + maximo + ".");
            } catch (NumberFormatException e) {
                error("Debes introducir un número entero.");
            }
        }
    }

    // Enter = fecha de hoy
    public static LocalDate leerFecha(String mensaje) {
        while (true) {
            String texto = leerLinea(mensaje + " (dd/mm/aaaa, Enter = hoy): ");
            if (texto.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(texto, FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                error("Fecha no válida. Formato: dd/mm/aaaa (ej: 05/11/2026).");
            }
        }
    }

    public static LocalDate leerFechaOpcional(String mensaje, LocalDate valorActual) {
        while (true) {
            String texto = leerLinea(mensaje + " [" + formatear(valorActual) + "]: ");
            if (texto.isEmpty()) {
                return valorActual;
            }
            try {
                return LocalDate.parse(texto, FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                error("Fecha no válida. Formato: dd/mm/aaaa (ej: 05/11/2026).");
            }
        }
    }

    // Muestra las opciones numeradas y devuelve la elegida (sirve para enums y arrays de texto)
    public static <E> E elegir(String mensaje, E[] opciones) {
        System.out.println(mensaje);
        for (int i = 0; i < opciones.length; i++) {
            System.out.println("  " + (i + 1) + ". " + opciones[i]);
        }
        return opciones[leerEntero("Opción: ", 1, opciones.length) - 1];
    }

    // Igual que elegir, pero con 0 se mantiene el valor actual
    public static <E> E elegirOpcional(String mensaje, E[] opciones, E valorActual) {
        System.out.println(mensaje);
        for (int i = 0; i < opciones.length; i++) {
            System.out.println("  " + (i + 1) + ". " + opciones[i]);
        }
        System.out.println("  0. Mantener [" + valorActual + "]");
        int opcion = leerEntero("Opción: ", 0, opciones.length);
        return opcion == 0 ? valorActual : opciones[opcion - 1];
    }

    public static boolean confirmar(String mensaje) {
        while (true) {
            String respuesta = leerLinea(mensaje + " (s/n): ").toLowerCase();
            if (respuesta.equals("s")) return true;
            if (respuesta.equals("n")) return false;
            error("Responde 's' o 'n'.");
        }
    }

    public static void pausa() {
        leerLinea("\nPulsa Enter para continuar...");
    }

    public static String formatear(LocalDate fecha) {
        return fecha == null ? "-" : fecha.format(FORMATO_FECHA);
    }
}
