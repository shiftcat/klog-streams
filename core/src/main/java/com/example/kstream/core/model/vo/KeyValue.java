package com.example.kstream.core.model.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;

public final class KeyValue<T> {
    private final String key;
    private final T value;

    public KeyValue(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public static <T> KeyValue<T> of(String key, T value) {
        return new KeyValue<>(key, value);
    }

    public String key() {
        return key;
    }

    public T value() {
        return value;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("key", key)
                .append("value", value)
                .toString();
    }
}
