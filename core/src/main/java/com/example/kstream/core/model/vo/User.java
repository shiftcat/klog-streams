package com.example.kstream.core.model.vo;

import com.example.kstream.core.utils.JsonUtil;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONObject;

import java.io.Serializable;

@Getter
public class User implements Serializable {

    private static final long serialVersionUID = -1922915566187644915L;

    private String id;
    private String ip;
    private String agent;

    public User() {
    }

    @Builder
    public User(String id, String ip, String agent) {
        this.id = id;
        this.ip = ip;
        this.agent = agent;
    }


    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", this.getId());
        jsonObject.put("ip", this.getIp());
        jsonObject.put("agent", this.getAgent());
        return jsonObject;
    }


    public static User from(JSONObject jsonObject) {
        return User.builder()
                .id(JsonUtil.getString(jsonObject, "id").orElse(null))
                .ip(JsonUtil.getString(jsonObject, "ip").orElse(null))
                .agent(JsonUtil.getString(jsonObject, "agent").orElse(null))
                .build();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return new EqualsBuilder()
                .append(id, user.id)
                .append(ip, user.ip)
                .append(agent, user.agent)
                .isEquals();
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id).append(ip).append(agent).toHashCode();
    }

}
