package com.example.kstream.core.serde;

import com.example.kstream.core.config.JsonConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class JsonDeserializer<T> implements Deserializer<T> {

    private ObjectMapper objectMapper = JsonConfig.objectMapper();

    @Override
    public T deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, new TypeReference<T>() {});
        } catch (IOException e) {
            e.getMessage();
        }
        return null;
    }
}
