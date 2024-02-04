package com.example.kstream.core.serde;

import com.example.kstream.core.model.dto.EventLog;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

public class EventLogSerializer implements Serializer<EventLog> {
    @Override
    public byte[] serialize(String topic, EventLog data) {
        return data.toJsonString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, EventLog data) {
        return serialize(topic, data);
    }
}
