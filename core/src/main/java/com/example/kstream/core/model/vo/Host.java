package com.example.kstream.core.model.vo;

import com.example.kstream.core.utils.JsonUtil;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONObject;

import java.io.Serializable;

@Getter
public class Host implements Serializable {

    private static final long serialVersionUID = 8540705183348876854L;

    private String name;
    private String ip;

    public Host() {
    }

    @Builder
    public Host(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }


    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", this.getName());
        jsonObject.put("ip", this.getIp());
        return jsonObject;
    }


    public static Host from(JSONObject jsonObject) {
        return Host.builder()
                .name(JsonUtil.getString(jsonObject, "name").orElse(null))
                .ip(JsonUtil.getString(jsonObject, "ip").orElse(null))
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Host host = (Host) o;
        return new EqualsBuilder()
                .append(name, host.name)
                .append(ip, host.ip)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(ip)
                .toHashCode();
    }
}
