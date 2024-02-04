package com.example.kstream.core.model.dto;

import com.example.kstream.core.utils.JsonUtil;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

import java.io.Serializable;

@Getter
public class ServiceLog implements Serializable {

    private String traceId;
    private String service;
    private EventLog reqLog;
    private EventLog resLog;

    @Builder
    public ServiceLog(String traceId, String service, EventLog reqLog, EventLog resLog) {
        this.traceId = traceId;
        this.service = service;
        this.reqLog = reqLog;
        this.resLog = resLog;
    }


    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("trace_id", this.getTraceId());
        jsonObject.put("service", this.getService());
        jsonObject.put("req_log", this.getReqLog().toJsonObject());
        jsonObject.put("res_log", this.getResLog().toJsonObject());
        return jsonObject;
    }


    public static ServiceLog from(JSONObject jsonObject) {
        return ServiceLog.builder()
                .traceId(JsonUtil.getString(jsonObject, "trace_id").orElse(null))
                .service(JsonUtil.getString(jsonObject, "service").orElse(null))
                .reqLog(EventLog.from(JsonUtil.getJSONObject(jsonObject, "req_log").orElse(new JSONObject())))
                .resLog(EventLog.from(JsonUtil.getJSONObject(jsonObject, "res_log").orElse(new JSONObject())))
                .build();
    }


    public boolean validate() {
        if(StringUtils.isEmpty(this.getTraceId())) {
            return false;
        }
        if (StringUtils.isEmpty(this.getService())) {
            return false;
        }
        if(this.getReqLog() == null || !this.getReqLog().validate()) {
            return false;
        }
        if(this.getResLog() == null || !this.getResLog().validate()) {
            return false;
        }
        return true;
    }


    public String toJsonString() {
        return this.toJSONObject().toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceLog that = (ServiceLog) o;
        return new EqualsBuilder()
                .append(traceId, that.traceId)
                .append(service, that.service)
                .append(reqLog, that.reqLog)
                .append(resLog, that.resLog)
                .isEquals();
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(traceId).append(service).append(reqLog).append(resLog).toHashCode();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("traceId", traceId)
                .append("service", service)
                .append("reqLog", reqLog)
                .append("resLog", resLog)
                .toString();
    }
}
