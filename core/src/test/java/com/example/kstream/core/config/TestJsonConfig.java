package com.example.kstream.core.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;

public class TestJsonConfig {

    private ObjectMapper objectMapper = JsonConfig.objectMapper();


    @Getter
    private static class Data {

        @JsonFormat(pattern = "yyyyMMdd'T'HHmmss")
        private Instant datetime;

        @JsonFormat(pattern = "yyyyMMdd'T'HHmmss")
        private LocalDateTime localDateTime;

        @Override
        public String toString() {
            return "Data{" +
                    "datetime=" + datetime +
                    ", localDateTime=" + localDateTime +
                    '}';
        }
    }


    @Test
    public void testSerialize() throws JsonProcessingException {
        Data data = new Data();
        data.datetime = Instant.now();
        data.localDateTime = LocalDateTime.now();

        String str = objectMapper.writeValueAsString(data);
        System.out.println(str);
    }


    @Test
    public void testDeserialize() throws JsonProcessingException {
        Data data = new Data();
        data.datetime = Instant.now();
        data.localDateTime = LocalDateTime.now();

        String str = objectMapper.writeValueAsString(data);
        Data readValue = objectMapper.readValue(str, Data.class);
        System.out.println(readValue);
    }

}
