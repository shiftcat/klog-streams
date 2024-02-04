package com.example.kstream.core.serde;

import com.example.kstream.core.model.dto.ServiceLog;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

public class ServiceLogSerializer implements Serializer<ServiceLog> {

    @Override
    public byte[] serialize(String topic, ServiceLog data) {
        return data.toJSONObject().toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, ServiceLog data) {
        return serialize(topic, data);
    }

}
