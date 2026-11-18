package org.example.util;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Conversión entre LocalDate (Java) y Date (tipo fecha de BSON/MongoDB).
 *
 * En MongoDB las fechas se guardan como Date en UTC; en Java trabajamos con
 * LocalDate porque los datos del refugio son días, sin hora.
 */
public final class Fechas {

    private Fechas() {
    }

    /** LocalDate -> Date (medianoche UTC). */
    public static Date aDate(LocalDate fecha) {
        if (fecha == null) {
            return null;
        }
        return Date.from(fecha.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    /** Date (UTC) -> LocalDate. */
    public static LocalDate aLocalDate(Date fecha) {
        if (fecha == null) {
            return null;
        }
        return fecha.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
    }
}