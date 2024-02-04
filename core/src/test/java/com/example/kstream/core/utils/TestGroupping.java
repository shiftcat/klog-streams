package com.example.kstream.core.utils;

import com.example.kstream.core.model.vo.KeyValue;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class TestGroupping {

    @Getter
    public static class ExampleGroup {
        private Long groupKey;
        private List<KeyValue<String>> keyValues;

        public ExampleGroup(Long groupKey, List<KeyValue<String>> keyValues) {
            this.groupKey = groupKey;
            this.keyValues = keyValues;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("groupKey", groupKey)
                    .append("keyValues", keyValues)
                    .toString();
        }
    }


    @Test
    public void testGroupping1() {
        KeyValue<String> kv1 = KeyValue.of("key1", "value1");
        KeyValue<String> kv2 = KeyValue.of("key2", "value2");
        KeyValue<String> kv3 = KeyValue.of("key3", "value3");
        KeyValue<String> kv4 = KeyValue.of("key4", "value4");
        KeyValue<String> kv5 = KeyValue.of("key5", "value5");

        ExampleGroup eg1 = new ExampleGroup(1L, Arrays.asList(kv1, kv2, kv3));
        ExampleGroup eg2 = new ExampleGroup(1L, Arrays.asList(kv4, kv5));
        ExampleGroup eg3 = new ExampleGroup(2L, Arrays.asList(kv2, kv4, kv5));

        List<ExampleGroup> list = Arrays.asList(eg1, eg2, eg3);

        for(ExampleGroup eg: list) {
            System.out.println(eg);
        }

        Map<Long, ArrayList<KeyValue<String>>> collect = list.stream()
                .collect(Collectors.groupingBy(eg -> eg.groupKey, Collector.of(
                        () -> new ArrayList<>(),
                        (l, g) -> l.addAll(g.getKeyValues()),
                        (l, r) -> {
                            l.addAll(r);
                            return l;
                        }
                )));

        Set<Long> keys = collect.keySet();
        Iterator<Long> iterator = keys.iterator();
        while (iterator.hasNext()) {
            Long next = iterator.next();
            ArrayList<KeyValue<String>> keyValues = collect.get(next);
            for(KeyValue<String> eg: keyValues) {
                System.out.println("group: " + next + " " + eg);
            }
        }
    }

}
