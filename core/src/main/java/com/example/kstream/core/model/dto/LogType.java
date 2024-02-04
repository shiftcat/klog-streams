package com.example.kstream.core.model.dto;

import java.util.Arrays;
import java.util.Optional;

public enum LogType
{

    IN_REQ("REQ", "100") {

        @Override
        public boolean isRequest() {
            return true;
        }

        @Override
        public LogType getPair() {
            return IN_RES;
        }
    }
    ,

    IN_RES("RES", "400") {

        @Override
        public boolean isRequest() {
            return false;
        }

        @Override
        public LogType getPair() {
            return IN_REQ;
        }
    }
    ,


    OUT_REQ("OUT_REQ", "300") {

        @Override
        public boolean isRequest() {
            return true;
        }

        @Override
        public LogType getPair() {
            return OUT_RES;
        }
    }
    ,

    OUT_RES("OUT_RES", "305") {

        @Override
        public boolean isRequest() {
            return false;
        }

        @Override
        public LogType getPair() {
            return OUT_REQ;
        }
    }
    ,


    OTHER("NONE", "300") {
        @Override
        public boolean isRequest() {
            return false;
        }

        @Override
        public LogType getPair() {
            return null;
        }
    }
    ;


    private String type;

    private String score;

    LogType(String type, String score)
    {
        this.type = type;
        this.score = score;
    }

    public abstract boolean isRequest();

    public abstract LogType getPair();

    public String getTypeString()
    {
        return type;
    }


    public String getScore()
    {
        return score;
    }


    public static LogType toValue(String logType)
    {
        Optional<LogType> first =
            Arrays.stream(LogType.values())
                .filter(t -> t.getTypeString().equals(logType))
                .findFirst();
        return first.orElse(OTHER);
    }

}
