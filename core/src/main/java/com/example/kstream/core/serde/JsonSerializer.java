package com.example.kstream.core.serde;

import com.example.kstream.core.config.JsonConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class JsonSerializer<T> implements Serializer<T> {

    private ObjectMapper objectMapper = JsonConfig.objectMapper();

    @Override
    public byte[] serialize(String s, T t) {
        try {
            return objectMapper.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            // throw new RuntimeException(e);
            e.printStackTrace();
        }
        return new byte[0];
    }

}
