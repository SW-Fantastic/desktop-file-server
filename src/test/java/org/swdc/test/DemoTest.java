package org.swdc.test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DemoTest {

    public static void main(String[] args) {
        System.err.println(LocalDateTime.now().atZone(ZoneId.of("UTC")).format(DateTimeFormatter
                .ofPattern("EEE, dd MMM yyyy hh:mm:ss zzz", Locale.ENGLISH)));
    }

}
