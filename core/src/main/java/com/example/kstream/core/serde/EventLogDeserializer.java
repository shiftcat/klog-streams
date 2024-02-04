package com.example.kstream.core.serde;

import com.example.kstream.core.model.dto.EventLog;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class EventLogDeserializer implements Deserializer<EventLog> {

    @Override
    public EventLog deserialize(String topic, byte[] data) {
        return EventLog.from(new JSONObject(new String(data, StandardCharsets.UTF_8)));
    }

    @Override
    public EventLog deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }
}
