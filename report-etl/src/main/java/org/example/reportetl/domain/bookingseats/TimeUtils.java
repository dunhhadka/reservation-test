package org.example.reportetl.domain.bookingseats;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class TimeUtils {

    public static long toLong(LocalDateTime time) {
        return time.toEpochSecond(ZoneOffset.UTC);
    }

}
