package org.swdc.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.RandomAccess;

public class DemoTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        RandomAccessFile file = new RandomAccessFile("README.md", "rw");
        file.getChannel().lock();
        Thread.sleep(1000 * 60 * 5);
    }

}
