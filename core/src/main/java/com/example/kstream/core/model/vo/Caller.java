package com.example.kstream.core.model.vo;

import com.example.kstream.core.utils.JsonUtil;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONObject;

import java.io.Serializable;

@Getter
public class Caller implements Serializable {
    private static final long serialVersionUID = -8684569450631174569L;

    private String channel;
    private String channelIp;

    public Caller() {
    }

    @Builder
    public Caller(String channel, String channelIp) {
        this.channel = channel;
        this.channelIp = channelIp;
    }


    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channel", this.getChannel());
        jsonObject.put("channelIp", this.getChannelIp());
        return jsonObject;
    }


    public static Caller from(JSONObject jsonObject) {
        return Caller.builder()
                .channel(JsonUtil.getString(jsonObject, "channel").orElse(null))
                .channelIp(JsonUtil.getString(jsonObject, "channelIp").orElse(null))
                .build();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Caller caller = (Caller) o;
        return new EqualsBuilder()
                .append(channel, caller.channel)
                .append(channelIp, caller.channelIp)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(channel)
                .append(channelIp)
                .toHashCode();
    }
}
