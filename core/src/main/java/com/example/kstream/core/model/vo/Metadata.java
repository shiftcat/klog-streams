package com.example.kstream.core.model.vo;

import com.example.kstream.core.utils.JsonUtil;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONObject;

import java.io.Serializable;

@Getter
public class Metadata implements Serializable {

    private final String topic;
    private final Integer partition;
    private final Long offset;

    @Builder
    public Metadata(String topic, Integer partition, Long offset) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
    }


    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("topic", this.getTopic());
        jsonObject.put("partition", this.getPartition());
        jsonObject.put("offset", this.getOffset());
        return jsonObject;
    }


    public static Metadata from(JSONObject json) {
        return Metadata.builder()
                .topic(JsonUtil.getString(json, "topic").orElse(""))
                .partition(JsonUtil.getInteger(json, "partition").orElse(-1))
                .offset(JsonUtil.getLong(json, "offset").orElse(-1L))
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metadata metadata = (Metadata) o;
        return new EqualsBuilder()
                .append(topic, metadata.topic)
                .append(partition, metadata.partition)
                .append(offset, metadata.offset)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(topic)
                .append(partition)
                .append(offset)
                .toHashCode();
    }

}
