package com.example.kstream.core.processor;

import com.example.kstream.core.model.dto.EventLog;
import com.example.kstream.core.model.vo.Metadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.util.Iterator;

@Slf4j
public class MetadataValueTransformer implements ValueTransformer<EventLog, EventLog> {

    private ProcessorContext context;

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public EventLog transform(EventLog eventLog) {
        Headers headers = context.headers();
        Iterator<Header> hiter = headers.iterator();
        while (hiter.hasNext()) {
            Header next = hiter.next();
            log.debug("Header key: {}, value: {}", next.key(), new String(next.value()));
        }
        eventLog.setMetadata(Metadata.builder()
                .topic(context.topic())
                .partition(context.partition())
                .offset(context.offset())
                .build());
        return eventLog;
    }

    @Override
    public void close() {

    }
}
