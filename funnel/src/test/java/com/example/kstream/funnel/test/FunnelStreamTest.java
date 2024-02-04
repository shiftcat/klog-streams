package com.example.kstream.funnel.test;

import com.example.kstream.funnel.topology.TraceLogStreamTopology;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.test.TestRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class FunnelStreamTest {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, String> inputTopic;
    private TestOutputTopic<String, String> outputTopic;


    @BeforeEach
    void setup() {
        // build the topology with a dummy client
        Topology topology = TraceLogStreamTopology.of("EVENT_LOG");

        // create a test driver. we will use this to pipe data to our topology
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        testDriver = new TopologyTestDriver(topology, props);

        // create the test input topic
        inputTopic =
                testDriver.createInputTopic(
                        "TOPIC_LOG_A", Serdes.String().serializer(), Serdes.String().serializer());

        // create the test output topic
        outputTopic =
                testDriver.createOutputTopic(
                        "TOPIC_LOGS02",
                        Serdes.String().deserializer(), Serdes.String().deserializer());
    }


    private final String[] logs = new String[] {
            "{\"log_type\":\"REQ\",\"trace_id\":\"6eeb5ecf7c5175fa681eaf32969d4e90\",\"span_id\":\"1\",\"service\":\"OU089012\",\"operation\":\"addParentalAgreement\",\"caller\":{\"channel\":\"SHUB_203.248.251.248:26001\",\"channelIp\":\"109.6.134.198\"},\"host\":{\"name\":\"BD-L2-SHALIN01\",\"ip\":\"104.148.146.43\"},\"destination\":{\"name\":\"DEST8\",\"ip\":\"81.251.179.254\"},\"user\":{\"id\":\"Britney\",\"ip\":\"202.135.76.236\",\"agent\":\"web\"},\"event_dt\":0}",
            "{\"log_type\":\"RES\",\"trace_id\":\"6eeb5ecf7c5175fa681eaf32969d4e90\",\"span_id\":\"1\",\"service\":\"OU089012\",\"operation\":\"addParentalAgreement\",\"caller\":{\"channel\":\"SHUB_203.248.251.248:26001\",\"channelIp\":\"109.6.134.198\"},\"host\":{\"name\":\"BD-L2-SHALIN01\",\"ip\":\"104.148.146.43\"},\"destination\":{\"name\":\"DEST8\",\"ip\":\"81.251.179.254\"},\"user\":{\"id\":\"Britney\",\"ip\":\"202.135.76.236\",\"agent\":\"web\"},\"event_dt\":0,\"response\":{\"type\":\"S\",\"status\":201,\"duration\":5329}}"
    };


    @Test
    public void testFunnelStream() {
        // https://docs.confluent.io/platform/current/streams/developer-guide/test-streams.html
        inputTopic.pipeInput("6eeb5ecf7c5175fa681eaf32969d4e90", logs[0]);
        testDriver.advanceWallClockTime(Duration.ofMillis(5000));
        inputTopic.pipeInput("6eeb5ecf7c5175fa681eaf32969d4e90", logs[1]);
        testDriver.advanceWallClockTime(Duration.ofMillis(500));

        List<TestRecord<String, String>> testRecords = outputTopic.readRecordsToList();
        testRecords.forEach(r -> {
            System.out.println(">> " + r.key() + r.value());
        });
    }

}
