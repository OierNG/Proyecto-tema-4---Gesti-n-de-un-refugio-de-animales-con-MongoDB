package org.example.util;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

/** Conversión LocalDate (Java) <-> Date (fecha BSON). Se guarda a medianoche UTC. */
public final class Fechas {

    private Fechas() {
    }

    public static Date aDate(LocalDate fecha) {
        if (fecha == null) {
            return null;
        }
        return Date.from(fecha.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    public static LocalDate aLocalDate(Date fecha) {
        if (fecha == null) {
            return null;
        }
        return fecha.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
    }
}