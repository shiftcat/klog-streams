package com.example.kstream.core.serde;

import com.example.kstream.core.model.dto.ServiceLog;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class ServiceLogDeserializer implements Deserializer<ServiceLog> {

    @Override
    public ServiceLog deserialize(String topic, byte[] data) {
        return ServiceLog.from(new JSONObject(new String(data, StandardCharsets.UTF_8)));
    }

    @Override
    public ServiceLog deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }

}
