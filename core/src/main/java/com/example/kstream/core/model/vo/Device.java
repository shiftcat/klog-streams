package com.example.kstream.core.model.vo;

import com.example.kstream.core.utils.JsonUtil;
import lombok.Builder;
import lombok.Getter;
import org.json.JSONObject;

import java.io.Serializable;

@Getter
public class Device implements Serializable {
    private final String id;

    @Builder
    public Device(String id) {
        this.id = id;
    }


    public static Device from(JSONObject jsonObject) {
        return Device.builder()
                .id(JsonUtil.getString(jsonObject, "id").orElse(null))
                .build();
    }

}
