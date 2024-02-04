package com.example.kstream.core.model.vo;

import com.example.kstream.core.model.dto.EventLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

public class TestEventLogJson {

    static final EventLog eventLog;

    static {
        long epochMilli = Instant.now().toEpochMilli();
        eventLog = EventLog.builder()
                .traceId("traceid")
                .spanId("spanId")
                .service("service")
                .logType("REQ")
                .operation("op-test")
                .unixTimestamp(epochMilli)
                .user(User.builder()
                        .id("userId")
                        .ip("192.168.100.1")
                        .agent("chrome")
                        .build())
                .host(Host.builder()
                        .name("test-host")
                        .ip("192.168.100.1.")
                        .build())
                .destination(Host.builder()
                        .name("test-host")
                        .ip("192.168.100.1.")
                        .build())
                .caller(Caller.builder()
                        .channel("test-channel")
                        .channelIp("127.0.0.1")
                        .build())
                .build();
    }

    private void prettyPrint(String str, Duration duration) {
        System.out.println(str + ": " + duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase());
    }


    /*
    ObjectMapper vs JSONObject 성능 비교 테스트(대략 4배 차이)
    ObjectMapper: 0.266s
    JSONObject: 0.052s
    */

    @Test
    public void test_EventLog_JSONObject() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            String json = eventLog.toJsonObject().toString();
            EventLog frommed = EventLog.from(new JSONObject(json));
            frommed.validate();
        }
        prettyPrint("JSONObject", Duration.ofMillis(System.currentTimeMillis() - start));
    }


    @Test
    public void test_EventLog_ObjectMapper() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            String json = objectMapper.writeValueAsString(eventLog);
            EventLog frommed = objectMapper.readValue(json, EventLog.class);
            frommed.validate();
        }
        prettyPrint("ObjectMapper", Duration.ofMillis(System.currentTimeMillis() - start));
    }


    @Test
    public void test_EventLog_Gson() {
        Gson gson = new Gson();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            String json = gson.toJson(eventLog);
            EventLog frommed = gson.fromJson(json, EventLog.class);
            frommed.validate();
        }
        prettyPrint("Gson", Duration.ofMillis(System.currentTimeMillis() - start));
    }
}
