package com.example.kstream.core.model.vo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;


public final class Pair<T1, T2> implements Serializable {
    private final T1 left;
    private final T2 right;

    public Pair(T1 left, T2 right) {
        this.left = left;
        this.right = right;
    }

    public static <T1, T2> Pair<T1, T2> of(T1 left, T2 right) {
        return new Pair<>(left, right);
    }


    public T1 left() {
        return left;
    }

    public T2 right() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return new EqualsBuilder()
                .append(left, pair.left)
                .append(right, pair.right)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(left).append(right).toHashCode();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("left", left)
                .append("right", right)
                .toString();
    }
}
