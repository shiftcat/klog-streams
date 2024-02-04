package com.example.kstream.core.serde;

import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

public class JavaSerializer<T> implements Serializer<T> {

    public byte[] serialize(String s, T t) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(t);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    public void close() {

    }
}
