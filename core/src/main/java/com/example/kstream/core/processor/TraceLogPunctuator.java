package com.example.kstream.core.processor;

import com.example.kstream.core.model.dto.TraceLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;


@Slf4j
public class TraceLogPunctuator implements Punctuator {

    private final ProcessorContext context;
    private final KeyValueStore<String, TraceLog> logsStore;

    public TraceLogPunctuator(ProcessorContext context, KeyValueStore<String, TraceLog> logsStore) {
        this.context = context;
        this.logsStore = logsStore;
    }


    private void emitLog(String key, TraceLog value) {
        log.info("emit log [key: {}, size: {}]", key, value.size());
        logsStore.delete(key);
        context.forward(key, value);
        context.commit();
    }

    @Override
    public void punctuate(long now) {
        try (KeyValueIterator<String, TraceLog> iterator = logsStore.all()) {
            while (iterator.hasNext()) {
                KeyValue<String, TraceLog> logsKeyValue = iterator.next();
                TraceLog traceLog = logsKeyValue.value;
                if(traceLog == null || traceLog.isEmpty()) {
                    logsStore.delete(logsKeyValue.key);
                    continue;
                }
                if(traceLog.afterEvictionDatetime(now)) {
                    emitLog(logsKeyValue.key, traceLog);
                    continue;
                }
                if(traceLog.beforeCheckDatetime(now)) {
                    continue;
                }

                boolean isOk = traceLog.checkTraceLog();
                traceLog.incrementCheckCount();
                if(isOk) {
                    emitLog(logsKeyValue.key, traceLog);
                }
                else {
                    traceLog.nextCheckDatetime();
                    if(traceLog.maxCheckCount()) {
                        emitLog(logsKeyValue.key, traceLog);
                    }
                    else {
                        logsStore.put(logsKeyValue.key, traceLog);
                    }
                }
            }
        }
    }

}
