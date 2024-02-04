package com.example.kstream.core.model.vo;

import com.example.kstream.core.aggregator.GroupKey;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONObject;

import java.io.Serializable;

@Getter
public class ServiceOperation implements Serializable, GroupKey {

    private String service;
    private String operation;

    public ServiceOperation() {
    }

    public ServiceOperation(String service, String operation) {
        this.service = service;
        this.operation = operation;
    }


    public static ServiceOperation from(String service, String operation) {
        return new ServiceOperation(service, operation);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceOperation that = (ServiceOperation) o;
        return new EqualsBuilder()
                .append(service, that.service)
                .append(operation, that.operation).isEquals();
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(service).append(operation).toHashCode();
    }


    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("service", this.service);
        jsonObject.put("operation", this.operation);
        return jsonObject;
    }


    @Override
    public String toString() {
        return toJSONObject().toString();
    }
}
