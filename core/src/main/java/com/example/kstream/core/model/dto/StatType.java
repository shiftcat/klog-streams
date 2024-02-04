package com.example.kstream.core.model.dto;

public enum StatType {

    SERVICE("SERVICE"),
    CHANNEL("CHANNEL"),
    SERVICE_OPERATION("SERVICE_OPERATION")
    ;

    private String code;

    StatType(String code) {
        this.code = code;
    }


    public boolean equalsString(String str) {
        return this.toString().equals(str);
    }

}
