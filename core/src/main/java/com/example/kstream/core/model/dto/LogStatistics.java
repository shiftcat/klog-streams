package com.example.kstream.core.model.dto;

import com.example.kstream.core.model.vo.StatWindow;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;


@Getter
public class LogStatistics<K> implements Serializable {

    private String statType;
    private K key;
    private StatWindow window;

    private long totalCount;

    private long successCount;
    private long userErrorCount;
    private long serverErrorCount;

    private long overOneCount;
    private long overThreeCount;

    private long minDuration;
    private long maxDuration;
    private double durationAverage;

    public LogStatistics() {
    }

    @Builder
    public LogStatistics(
            String statType, K key, StatWindow window,
            long totalCount, long successCount, long userErrorCount, long serverErrorCount,
            long overOneCount, long overThreeCount,
            long minDuration, long maxDuration, double durationAverage
    ) {
        this.statType = statType;
        this.key = key;
        this.window = window;
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.userErrorCount = userErrorCount;
        this.serverErrorCount = serverErrorCount;
        this.overOneCount = overOneCount;
        this.overThreeCount = overThreeCount;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.durationAverage = durationAverage;
    }

    public boolean validate() {
        if(StringUtils.isEmpty(this.statType)) {
            return false;
        }
        if(this.key == null) {
            return false;
        }
        if(this.window == null) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("statType", statType)
                .append("key", key)
                .append("window", window)
                .append("totalCount", totalCount)
                .append("successCount", successCount)
                .append("userErrorCount", userErrorCount)
                .append("serverErrorCount", serverErrorCount)
                .append("overOneCount", overOneCount)
                .append("overThreeCount", overThreeCount)
                .append("minDuration", minDuration)
                .append("maxDuration", maxDuration)
                .append("durationAverage", durationAverage)
                .toString();
    }
}
