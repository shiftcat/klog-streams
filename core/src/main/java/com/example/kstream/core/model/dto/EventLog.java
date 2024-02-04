package com.example.kstream.core.model.dto;


import com.example.kstream.core.model.vo.*;
import com.example.kstream.core.utils.JsonUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

import java.io.Serializable;

@Slf4j
@Getter
public class EventLog implements Serializable {

    private static final long serialVersionUID = 7169563066056972628L;

    private String logType;

    private String traceId;
    private String spanId;
    private String service;
    private String operation;

    private Caller caller;
    private Host host;
    private Host destination;
    private User user;
    private Response response;

    private long unixTimestamp;

    private Metadata metadata;

    @JsonIgnore
    private EventLog reqLog;

    public EventLog() {
    }

    @Builder(builderClassName = "LogBuilder")
    private EventLog(
            String logType, String traceId, String spanId, String service, String operation,
            Caller caller, Host host, Response response, User user, Host destination,
            long unixTimestamp
    ) {
        this.logType = logType;
        this.traceId = traceId;
        this.spanId = spanId;
        this.service = service;
        this.operation = operation;
        this.caller = caller;
        this.host = host;
        this.response = response;
        this.user = user;
        this.destination = destination;
        this.unixTimestamp = unixTimestamp;
    }


    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public void setReqLog(EventLog reqLog) {
        this.reqLog = reqLog;
    }

    // 필수 컬럼 값 체크
    public boolean validate() {
        if (StringUtils.isEmpty(this.getTraceId())) {
            log.warn("TraceId value is empty.");
            return false;
        }
        if (StringUtils.isEmpty(this.getSpanId())) {
            log.warn("SpanId value is empty.");
            return false;
        }
        if (StringUtils.isEmpty(this.getLogType())) {
            log.warn("LogType value is empty.");
            return false;
        }
        if (StringUtils.isEmpty(this.getService())) {
            log.warn("Service value is empty.");
            return false;
        }
        if (StringUtils.isEmpty(this.getOperation())) {
            log.warn("Operation value is empty.");
            return false;
        }
        if (this.getHost() == null) {
            log.warn("Host is null.");
            return false;
        } else {
            if (StringUtils.isEmpty(this.getHost().getName())) {
                log.warn("Host name value is empty.");
                return false;
            }
        }
        if (this.getUnixTimestamp() == -1) {
            log.warn("Timestamp value is wrong.");
            return false;
        }
        return true;
    }


    public static EventLog from(JSONObject jsonObj) {
        EventLog.LogBuilder builder = EventLog.builder()
                .unixTimestamp(JsonUtil.getLong(jsonObj, "event_dt").orElse(-1L))
                .service(JsonUtil.getString(jsonObj, "service").orElse(""))
                .operation(JsonUtil.getString(jsonObj, "operation").orElse(""))
                .traceId(JsonUtil.getString(jsonObj, "trace_id").orElse(""))
                .spanId(JsonUtil.getString(jsonObj, "span_id").orElse(""))
                .logType(JsonUtil.getString(jsonObj, "log_type").orElse(""));

        if (jsonObj.has("caller")) {
            Caller caller = JsonUtil.getJSONObject(jsonObj, "caller")
                    .map(Caller::from)
                    .orElse(null);
            builder.caller(caller);
        }
        if (jsonObj.has("host")) {
            Host host = JsonUtil.getJSONObject(jsonObj, "host")
                    .map(Host::from)
                    .orElse(null);
            builder.host(host);
        }
        if (jsonObj.has("response")) {
            Response response = JsonUtil.getJSONObject(jsonObj, "response")
                    .map(Response::from)
                    .orElse(null);
            builder.response(response);
        }
        if (jsonObj.has("user")) {
            User user = JsonUtil.getJSONObject(jsonObj, "user")
                    .map(User::from)
                    .orElse(null);
            builder.user(user);
        }
        if (jsonObj.has("destination")) {
            Host destination = JsonUtil.getJSONObject(jsonObj, "destination")
                    .map(Host::from)
                    .orElse(null);
            builder.destination(destination);
        }

        if(jsonObj.has("metadata")) {
            JsonUtil.getJSONObject(jsonObj, "metadata")
                    .map(Metadata::from)
                    .orElse(null);
        }

        return builder.build();
    }



    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();

        jsonObj.put("trace_id", this.getTraceId());
        jsonObj.put("span_id", this.getSpanId());
        jsonObj.put("log_type", this.getLogType());
        jsonObj.put("service", this.getService());
        jsonObj.put("operation", this.getOperation());
        jsonObj.put("event_dt", this.getUnixTimestamp());

        if (this.getCaller() != null) {
            jsonObj.put("caller", this.getCaller().toJSONObject());
        }
        if (this.getHost() != null) {
            jsonObj.put("host", this.getHost().toJSONObject());
        }
        if (this.getResponse() != null) {
            jsonObj.put("response", this.getResponse().toJSONObject());
        }
        if (this.getUser() != null) {
            jsonObj.put("user", this.getUser().toJSONObject());
        }
        if (this.getDestination() != null) {
            jsonObj.put("destination", this.getDestination().toJSONObject());
        }
        if (this.metadata != null) {
            jsonObj.put("metadata", this.metadata.toJSONObject());
        }

        return jsonObj;
    }


    public String toJsonString() {
        return toJsonObject().toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventLog eventLog = (EventLog) o;
        return new EqualsBuilder()
                .append(traceId, eventLog.traceId)
                .append(spanId, eventLog.spanId)
                .append(service, eventLog.service)
                .append(operation, eventLog.operation)
                .append(caller, eventLog.caller)
                .append(host, eventLog.host)
                .append(destination, eventLog.destination)
                .append(user, eventLog.user)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(traceId)
                .append(spanId)
                .append(service)
                .append(operation)
                .append(caller)
                .append(host)
                .append(destination)
                .append(user)
                .toHashCode();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("logType", logType)
                .append("traceId", traceId)
                .append("spanId", spanId)
                .append("service", service)
                .append("operation", operation)
                .append("caller", caller)
                .append("host", host)
                .append("destination", destination)
                .append("user", user)
                .append("response", response)
                .append("unixTimestamp", unixTimestamp)
                .toString();
    }
}
