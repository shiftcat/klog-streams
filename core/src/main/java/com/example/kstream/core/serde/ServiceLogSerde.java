package com.example.kstream.core.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import com.example.kstream.core.model.dto.ServiceLog;


public class ServiceLogSerde implements Serde<ServiceLog> {

    @Override
    public Serializer<ServiceLog> serializer() {
        return new ServiceLogSerializer();
    }

    @Override
    public Deserializer<ServiceLog> deserializer() {
        return new ServiceLogDeserializer();
    }

}
