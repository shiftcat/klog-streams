package com.example.kstream.funnel;


import com.example.kstream.funnel.topology.TraceLogStreamTopology;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;


public class App {

    private final static Logger log = LoggerFactory.getLogger(com.example.kstream.funnel.App.class);


    private static Properties getProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "funnel");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "3");
        return props;
    }


    public static void main(String[] args) {
        log.info("Kafka stream application start...");
        Properties prop = getProperties();
        Topology topology = TraceLogStreamTopology.of("EVENT_LOG");

        try(KafkaStreams kafkaStreams = new KafkaStreams(topology, prop)) {
            CountDownLatch latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                kafkaStreams.close();
                log.info("System... close.");
                latch.countDown();
            }));

            kafkaStreams.start();
            latch.await();
            System.exit(0);
        }
        catch (InterruptedException ex) {
            System.exit(1);
        }
    }
}
