package com.example.kstream.bucket.topology;

import com.example.kstream.core.aggregator.LogAggregator;
import com.example.kstream.core.model.dto.ServiceStatistics;
import com.example.kstream.core.model.dto.ServiceLog;
import com.example.kstream.core.model.dto.StatType;
import com.example.kstream.core.model.vo.ServiceOperation;
import com.example.kstream.core.model.vo.StatWindow;
import com.example.kstream.core.serde.JavaSerde;
import com.example.kstream.core.serde.JsonSerde;
import com.example.kstream.core.serde.ServiceLogSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowBytesStoreSupplier;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;

public class BucketStreamTopology {

    private final static Logger log = LoggerFactory.getLogger(com.example.kstream.bucket.topology.BucketStreamTopology.class);


    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    private final StreamsBuilder streamsBuilder;

    private final Serde<String> stringSerde = Serdes.String();
    private final JavaSerde<ServiceLog> serviceLogJerde = new JavaSerde<>();
    private final ServiceLogSerde serviceLogSerde = new ServiceLogSerde();
    private final Serde<Windowed<String>> windowedSerde = WindowedSerdes.timeWindowedSerdeFrom(String.class);
    private final Serde<Windowed<ServiceOperation>> windowJsonSerde = new WindowedSerdes.TimeWindowedSerde<>(new JsonSerde<>());
    private final JavaSerde<ServiceStatistics> serviceStatisticsSerde = new JavaSerde<>();
    private final JsonSerde<ServiceStatistics> outputValueSerde = new JsonSerde<>();

    private static final Duration WINDOW_SIZE = Duration.ofMinutes(1);
    private static final Duration WINDOW_GRACE = Duration.ofSeconds(10);

    private static final String OUTPUT_TOPIC = "TOPIC_STAT";


    public BucketStreamTopology(StreamsBuilder streamsBuilder) {
        this.streamsBuilder = streamsBuilder;
    }


    public static Topology of(String sourceTopic) {
        StreamsBuilder builder = new StreamsBuilder();
        new BucketStreamTopology(builder).createTopology(sourceTopic);
        return builder.build();
    }


    private void serviceStatStream(KStream<String, ServiceLog> stream) {
        WindowBytesStoreSupplier serviceStatStore =
                Stores.inMemoryWindowStore("service-stat-store", Duration.ofMinutes(10), WINDOW_SIZE, true);

        Materialized<String, ServiceStatistics, WindowStore<Bytes, byte[]>> materialized =
                Materialized.<String, ServiceStatistics>as(serviceStatStore)
                        .withKeySerde(stringSerde).withValueSerde(serviceStatisticsSerde);

        Initializer<ServiceStatistics> aggregateInitializer = () -> new ServiceStatistics(StatType.SERVICE);

        // Service 통계
        KTable<Windowed<String>, ServiceStatistics> serviceAgg =
                stream.groupByKey(Grouped.with(stringSerde, serviceLogJerde))
                        .windowedBy(TimeWindows.of(WINDOW_SIZE).grace(WINDOW_GRACE))
                        .aggregate(aggregateInitializer, new LogAggregator<String>(), materialized
                                // Materialized.with(stringSerde, new JavaSerde<LogAggregate>())
                        )
                        .suppress(Suppressed.untilWindowCloses(unbounded()));
//                .aggregate(aggregateInitializer, new LogAggregator());

        serviceAgg.toStream()
                .peek((k, v) -> v.setWindow(StatWindow.of(k.window().startTime(), k.window().endTime())))
                .peek((k, v) -> log.info("Service statistics: [{}~{}] {}",
                        formatter.format(k.window().startTime()), formatter.format(k.window().endTime()), v.toJSONObject()))
                .to(OUTPUT_TOPIC, Produced.with(windowedSerde, outputValueSerde));
    }


    private void channelStatStream(KStream<String, ServiceLog> stream) {
        // Channel 통계
        stream
                .groupBy((k, v) -> v.getResLog().getCaller().getChannel(), Grouped.with(stringSerde, serviceLogJerde))
                .windowedBy(TimeWindows.of(WINDOW_SIZE).grace(WINDOW_GRACE))
                .aggregate(() -> new ServiceStatistics(StatType.CHANNEL),
                        new LogAggregator<String>(),
                        Materialized.with(stringSerde, serviceStatisticsSerde))
                .suppress(Suppressed.untilWindowCloses(unbounded()))
                .toStream()
                .peek((k, v) -> v.setWindow(StatWindow.of(k.window().startTime(), k.window().endTime())))
                .peek((k, v) -> log.info("Channel statistics: [{}~{}] {}",
                        formatter.format(k.window().startTime()), formatter.format(k.window().endTime()), v.toJSONObject()))
                .to(OUTPUT_TOPIC, Produced.with(windowedSerde, outputValueSerde));
    }


    private void serviceOperationStatStream(KStream<String, ServiceLog> stream) {
        JavaSerde<ServiceOperation> serviceOperationSerde = new JavaSerde<>();
        // Service, Operation 통계
        stream
                .groupBy((k, v) -> {
                    String operation = v.getResLog().getOperation();
                    return new ServiceOperation(k, operation);
                }, Grouped.with(serviceOperationSerde, serviceLogJerde))
                .windowedBy(TimeWindows.of(WINDOW_SIZE).grace(WINDOW_GRACE))
                .aggregate(() -> new ServiceStatistics(StatType.SERVICE_OPERATION), new LogAggregator<ServiceOperation>(),
                        Materialized.with(serviceOperationSerde, serviceStatisticsSerde))
                .suppress(Suppressed.untilWindowCloses(unbounded()))
                .toStream()
                .peek((k, v) -> v.setWindow(StatWindow.of(k.window().startTime(), k.window().endTime())))
                .peek((k, v) -> log.info("ServiceOperation statistics: [{}~{}] {}",
                        formatter.format(k.window().startTime()), formatter.format(k.window().endTime()), v.toJSONObject()))
                .to(OUTPUT_TOPIC, Produced.with(windowJsonSerde, outputValueSerde));
    }


    public void createTopology(String sourceTopic) {
        KStream<String, ServiceLog> stream =
                streamsBuilder.stream(sourceTopic, Consumed.with(stringSerde, serviceLogSerde)
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                        .filter((k, v) -> v.validate());

        serviceStatStream(stream);
        channelStatStream(stream);
        serviceOperationStatStream(stream);
    }


}
