package com.example.kstream.core.utils;

import org.apache.kafka.common.utils.Utils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

public class TestMurmur {

    @Test
    public void testMurmur_1() {
        byte[] bytes = "PG004040".getBytes(StandardCharsets.UTF_8);
//        byte[] bytes = "foobar".getBytes(StandardCharsets.UTF_8);
        int i = Utils.murmur2(bytes);
        System.out.println(i);
    }

    private int partition(byte[] keyBytes, int numPartitions) {
        return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
    }

    @Test
    public void testDefaultPartitioner1() {
        byte[] bytes = "PG004040".getBytes(StandardCharsets.UTF_8);
        int partition = partition(bytes, 3);
        System.out.println(partition);
    }

    @Test
    public void testDefaultPartitioner2() {
        byte[] bytes = "AP064210".getBytes(StandardCharsets.UTF_8);
        int partition = partition(bytes, 3);
        System.out.println(partition);
    }

    @Test
    public void testDefaultPartitioner3() {
        byte[] bytes = "OU089012".getBytes(StandardCharsets.UTF_8);
        int partition = partition(bytes, 3);
        System.out.println(partition);
    }
}
