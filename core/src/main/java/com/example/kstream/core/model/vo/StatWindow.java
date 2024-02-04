package com.example.kstream.core.model.vo;

import com.example.kstream.core.config.JsonConfig;
import com.example.kstream.core.utils.JsonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;

@Getter
public class StatWindow implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Instant start;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Instant end;

    public StatWindow() {
    }

    public StatWindow(Instant start, Instant end) {
        this.start = start;
        this.end = end;
    }

    public static StatWindow of(Instant start, Instant end) {
        return new StatWindow(start, end);
    }


    public static StatWindow from(JSONObject jsonObject) {
         Instant start = JsonUtil.getString(jsonObject, "start").map(s -> Instant.from(JsonConfig.dateTimeFormatter.parse(s))).orElse(null);
         Instant end = JsonUtil.getString(jsonObject, "end").map(s -> Instant.from(JsonConfig.dateTimeFormatter.parse(s))).orElse(null);
         return StatWindow.of(start, end);
    }


    public boolean validate() {
        if(this.start == null || this.end == null) {
            return false;
        }
        return true;
    }


    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("start", JsonConfig.dateTimeFormatter.format(this.start));
        jsonObject.put("end", JsonConfig.dateTimeFormatter.format(this.end));
        return jsonObject;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatWindow that = (StatWindow) o;
        return new EqualsBuilder()
                .append(start, that.start)
                .append(end, that.end)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(start).append(end).toHashCode();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("start", start)
                .append("end", end)
                .toString();
    }

}
