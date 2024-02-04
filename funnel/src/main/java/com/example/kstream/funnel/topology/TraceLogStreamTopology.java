package com.example.kstream.funnel.topology;

import com.example.kstream.core.model.dto.EventLog;
import com.example.kstream.core.model.dto.LogType;
import com.example.kstream.core.model.dto.ServiceLog;
import com.example.kstream.core.model.dto.TraceLog;
import com.example.kstream.core.processor.MetadataValueTransformer;
import com.example.kstream.core.processor.TraceLogTransformer;
import com.example.kstream.core.serde.EventLogSerde;
import com.example.kstream.core.serde.JavaSerde;
import com.example.kstream.core.serde.ServiceLogSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class TraceLogStreamTopology {

    private final static Logger log = LoggerFactory.getLogger(com.example.kstream.funnel.topology.TraceLogStreamTopology.class);

    private final Serde<String> stringSerde = Serdes.String();
    private final EventLogSerde eventLogSerde = new EventLogSerde();
    private final ServiceLogSerde serviceLogSerde = new ServiceLogSerde();

    private final StreamsBuilder streamsBuilder;

    public TraceLogStreamTopology(StreamsBuilder streamsBuilder) {
        this.streamsBuilder = streamsBuilder;
    }


    public static Topology of(String sourceTopic) {
        KeyValueBytesStoreSupplier keyValueStore = Stores.inMemoryKeyValueStore("logs-store");
        StoreBuilder<KeyValueStore<String, TraceLog>> storeBuilder =
                Stores.keyValueStoreBuilder(keyValueStore, Serdes.String(), new JavaSerde<>());

        StreamsBuilder builder = new StreamsBuilder();
        builder.addStateStore(storeBuilder);
        new TraceLogStreamTopology(builder).createTopology(sourceTopic, keyValueStore.name());
        return builder.build();
    }


    // 1. 정상 데이터 처리
    private void successStream(KStream<String, TraceLog> stream) {
        stream.flatMapValues(logs -> {
                    List<EventLog> eventLogs = logs.getEventLogs();
                    return eventLogs.stream()
                            .filter(l -> LogType.IN_RES.getTypeString().equals(l.getLogType()))
                            .filter(l -> l.getReqLog() != null) // Res에 대응하는 Req 로그
                            .map(l ->
                                    ServiceLog.builder()
                                            .traceId(l.getTraceId())
                                            .service(l.getService())
                                            .reqLog(l.getReqLog())
                                            .resLog(l).build()
                            )
                            .collect(Collectors.toList());
                })
                .selectKey((k ,v) -> v.getService())
                .to("TOPIC_LOGS", Produced.with(stringSerde, serviceLogSerde));
    }


    // 2. 비정상 데이터 처리
    private void failStream(KStream<String, TraceLog> stream) {
        // 비정상 데이터는 파일 또는 Database 등에 저장 후 재처리 등 고려
        // branch[1].print(Printed.toFile("./fail.log"));
        stream.print(Printed.toSysOut());
    }


    public void createTopology(String sourceTopic, String storeName) {

        KStream<String, TraceLog>[] branch =
                streamsBuilder.stream(sourceTopic, Consumed.with(stringSerde, eventLogSerde).withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                        .peek((k ,v) -> System.out.println(v))
                        .transformValues(MetadataValueTransformer::new)
                        .filter((k, v) -> v.validate())
                        // TraceId 별 데이터 수집
                        .transform(() -> new TraceLogTransformer(storeName), storeName)
                        .filter((k, v) -> !v.isEmpty())
                        // 정상/비정상 스트림 분기
                        .branch(
                                (k, v) -> v.isValid(),
                                (k, v) -> !v.isValid()
                        );

        successStream(branch[0]);
        failStream(branch[1]);
    }

}
