package com.example.kstream.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public class TestDatetimeFormatter {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Test
    public void testFormatDatetime() {
        log.info(LocalDateTime.now().format(formatter));
    }

    @Test
    public void testTimestampFormatString() {
        long current = System.currentTimeMillis();
        String format = formatter.format(Instant.ofEpochMilli(current).atZone(ZoneId.systemDefault()));
        log.info(format);
    }
}
