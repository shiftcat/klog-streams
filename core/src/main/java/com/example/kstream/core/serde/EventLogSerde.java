package com.example.kstream.core.serde;

import com.example.kstream.core.model.dto.EventLog;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class EventLogSerde implements Serde<EventLog> {
    @Override
    public Serializer<EventLog> serializer() {
        return new EventLogSerializer();
    }

    @Override
    public Deserializer<EventLog> deserializer() {
        return new EventLogDeserializer();
    }
}
