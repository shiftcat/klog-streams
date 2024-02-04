package com.example.kstream.core.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class JsonConfig {

    private static final String yyyMMddHHmmss = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String yyyMMdd = "yyyy-MM-dd";

    private static final ZoneId zoneId = ZoneId.of("Asia/Seoul");

    public static DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern(yyyMMddHHmmss).withZone(zoneId);

    public static Module jsonMapperJava8DateTimeModule() {
        JavaTimeModule module = new JavaTimeModule();

        module.addDeserializer(LocalDate.class, new com.fasterxml.jackson.databind.JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                return LocalDate.parse(jsonParser.getValueAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        });

        module.addDeserializer(LocalTime.class, new com.fasterxml.jackson.databind.JsonDeserializer<LocalTime>() {
            @Override
            public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                return LocalTime.parse(jsonParser.getValueAsString(), DateTimeFormatter.ofPattern("kk:mm:ss"));
            }
        });

        module.addDeserializer(LocalDateTime.class, new com.fasterxml.jackson.databind.JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                return LocalDateTime.parse(jsonParser.getValueAsString(), dateTimeFormatter);
            }
        });

        module.addDeserializer(Instant.class, new com.fasterxml.jackson.databind.JsonDeserializer<Instant>() {
            @Override
            public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                LocalDateTime localDateTime = LocalDateTime.parse(jsonParser.getValueAsString(), dateTimeFormatter);
                return Instant.from(localDateTime.atZone(zoneId).toInstant());
            }
        });


        module.addSerializer(Instant.class, new com.fasterxml.jackson.databind.JsonSerializer<Instant>() {
            @Override
            public void serialize(Instant value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                String str = dateTimeFormatter.format(value);
                jsonGenerator.writeString(str);
            }
        });

        module.addSerializer(LocalDateTime.class, new com.fasterxml.jackson.databind.JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                String str = dateTimeFormatter.format(value);
                jsonGenerator.writeString(str);
            }
        });

        return module;
    }


    public static ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(jsonMapperJava8DateTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
                ;
    }
}
