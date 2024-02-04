package com.example.kstream.core.model.vo;

import com.example.kstream.core.utils.JsonUtil;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONObject;

import java.io.Serializable;

@Getter
public class Response implements Serializable {

    private static final long serialVersionUID = 1944925958251956384L;

    private String type;
    private Integer status;
    private String desc;
    private Long duration;

    public Response() {
    }

    @Builder
    public Response(String type, Integer status, String desc, Long duration) {
        this.type = type;
        this.status = status;
        this.desc = desc;
        this.duration = duration;
    }


    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", this.getType());
        jsonObject.put("status", this.getStatus());
        jsonObject.put("desc", this.getDesc());
        jsonObject.put("duration", this.getDuration());
        return jsonObject;
    }


    public static Response from(JSONObject jsonObject) {
        return Response.builder()
                .type(JsonUtil.getString(jsonObject, "type").orElse(null))
                .status(JsonUtil.getInteger(jsonObject, "status").orElse(null))
                .desc(JsonUtil.getString(jsonObject, "desc").orElse(null))
                .duration(JsonUtil.getLong(jsonObject, "duration").orElse(null))
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return new EqualsBuilder()
                .append(type, response.type)
                .append(status, response.status)
                .append(desc, response.desc)
                .append(duration, response.duration)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(type)
                .append(status)
                .append(desc)
                .append(duration)
                .toHashCode();
    }

}
