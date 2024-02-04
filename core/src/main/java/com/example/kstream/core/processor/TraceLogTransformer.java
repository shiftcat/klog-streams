package com.example.kstream.core.processor;

import com.example.kstream.core.model.dto.EventLog;
import com.example.kstream.core.model.dto.TraceLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.Duration;

@Slf4j
public class TraceLogTransformer implements Transformer<String, EventLog, KeyValue<String, TraceLog>> {

    private KeyValueStore<String, TraceLog> logsStore;
    private final String stateStoreName;
    public TraceLogTransformer(String storeName) {
        stateStoreName = storeName;
    }


    @Override
    public void init(ProcessorContext context) {
        logsStore = (KeyValueStore<String, TraceLog>) context.getStateStore(stateStoreName);
        TraceLogPunctuator traceLogPunctuator = new TraceLogPunctuator(context, logsStore);
        Duration duration = Duration.ofMillis(500);
        context.schedule(duration, PunctuationType.WALL_CLOCK_TIME, traceLogPunctuator);
    }


    @Override
    public KeyValue<String, TraceLog> transform(String key, EventLog eventLog) {
        TraceLog traceLog = logsStore.get(key);
        if(traceLog == null) {
            traceLog = new TraceLog(key, eventLog);
        } else {
            traceLog.addLog(eventLog);
        }
        logsStore.put(key, traceLog);
        return null;
    }


    @Override
    public void close() {
        logsStore.flush();
    }
}
