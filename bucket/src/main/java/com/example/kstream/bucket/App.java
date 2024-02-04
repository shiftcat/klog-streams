package com.example.kstream.bucket;

import com.example.kstream.bucket.topology.BucketStreamTopology;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class App {

    private static Properties getProperties() {
        Properties prop = new Properties();
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG, "bucket");
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        prop.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        prop.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "3");
        return prop;
    }


    public static void main(String[] args) {
        Properties prop = getProperties();
        Topology topology = BucketStreamTopology.of("TOPIC_LOGS");

        try (KafkaStreams kafkaStreams = new KafkaStreams(topology, prop)) {
            CountDownLatch latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                kafkaStreams.close();
                log.info("System... close.");
                latch.countDown();
            }));

            kafkaStreams.start();
            latch.await();
            System.exit(0);
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }
}
