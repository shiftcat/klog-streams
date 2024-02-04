package com.example.kstream.core.model.dto;

import com.example.kstream.core.aggregator.GroupKey;
import com.example.kstream.core.utils.JsonUtil;
import com.example.kstream.core.model.vo.StatWindow;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public class ServiceStatistics implements Serializable {

    private StatType statType;
    private Object key;
    private StatWindow window;

    private long totalCount;

    private long successCount;
    private long userErrorCount;
    private long serverErrorCount;

    private long overOneCount;
    private long overThreeCount;

    private long minDuration;
    private long maxDuration;

    @JsonIgnore
    private long totalDuration;
    private double durationAverage;

    private Set<ServiceLog> logs;

    public ServiceStatistics(StatType statType) {
        this.statType = statType;
        this.minDuration = Long.MAX_VALUE;
        this.logs = new LinkedHashSet<>();
    }

    public void addServiceLog(ServiceLog serviceLog) {
        this.logs.add(serviceLog);
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public void setWindow(StatWindow window) {
        this.window = window;
    }

    public void incrementTotalCount() {
        this.totalCount++;
    }

    public void incrementSuccessCount() {
        this.successCount++;
    }

    public void incrementUserErrorCount() {
        this.userErrorCount++;
    }

    public void incrementServerErrorCount() {
        this.serverErrorCount++;
    }

    public void incrementOverOneCount() {
        this.overOneCount++;
    }

    public void incrementOverThreeCount() {
        this.overThreeCount++;
    }

    public void setMinDuration(long duration) {
        if(this.minDuration > duration) {
            this.minDuration = duration;
        }
    }

    public void setMaxDuration(long duration) {
        if(this.maxDuration < duration) {
            this.maxDuration = duration;
        }
    }

    public void addDuration(long duration) {
        this.totalDuration += duration;
    }

    public void calculateDurationAverage() {
        this.durationAverage = this.totalDuration / (double)this.totalCount;
    }


    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        if(this.key instanceof GroupKey) {
            jsonObject.put("key", ((GroupKey)this.key).toJSONObject());
        }
        else {
            jsonObject.put("key", this.key);
        }
        jsonObject.put("window", this.window.toJSONObject());
        jsonObject.put("total_count", this.totalCount);
        jsonObject.put("success_count", this.successCount);
        jsonObject.put("user_error", this.userErrorCount);
        jsonObject.put("server_error", this.serverErrorCount);
        jsonObject.put("over_one", this.overOneCount);
        jsonObject.put("over_three", this.overThreeCount);
        jsonObject.put("min_duration", this.minDuration);
        jsonObject.put("max_duration", this.maxDuration);
        jsonObject.put("duration_avg", this.durationAverage);
        return jsonObject;
    }


    public static ServiceStatistics from(JSONObject jsonObject) {
        String statTypeString = JsonUtil.getString(jsonObject, "stat_type").orElse(null);
        StatType statType = StatType.valueOf(statTypeString);
        ServiceStatistics agg = new ServiceStatistics(statType);
        agg.key = JsonUtil.getString(jsonObject, "key").orElse(null);
        agg.window = StatWindow.from(JsonUtil.getJSONObject(jsonObject, "window").orElse(new JSONObject()));
        agg.totalCount = JsonUtil.getLong(jsonObject, "total_count").orElse(-1L);
        agg.successCount = JsonUtil.getLong(jsonObject, "success_count").orElse(-1L);
        agg.userErrorCount = JsonUtil.getLong(jsonObject, "user_error").orElse(-1L);
        agg.serverErrorCount = JsonUtil.getLong(jsonObject, "server_error").orElse(-1L);
        agg.overOneCount = JsonUtil.getLong(jsonObject, "over_one").orElse(-1L);
        agg.overThreeCount = JsonUtil.getLong(jsonObject, "over_three").orElse(-1L);
        agg.minDuration = JsonUtil.getLong(jsonObject, "min_duration").orElse(-1L);
        agg.maxDuration = JsonUtil.getLong(jsonObject, "max_duration").orElse(-1L);
        agg.durationAverage = JsonUtil.getDouble(jsonObject, "duration_avg").orElse(-1D);
        return agg;
    }


    public boolean validate() {
        if(this.statType == null) {
            return false;
        }
        if(this.key == null) {
            return false;
        }
        if(this.window == null) {
            return false;
        } else if(!this.window.validate()) {
            return false;
        }
        if(this.totalCount < 0) {
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
                .append("totalDuration", totalDuration)
                .append("durationAverage", durationAverage)
                .toString();
    }
}
