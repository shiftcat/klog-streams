package com.example.kstream.core.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class JavaSerde<T> implements Serde<T> {
    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<T> serializer() {
        return new JavaSerializer<>();
    }

    @Override
    public Deserializer<T> deserializer() {
        return new JavaDeserializer<>();
    }
}
