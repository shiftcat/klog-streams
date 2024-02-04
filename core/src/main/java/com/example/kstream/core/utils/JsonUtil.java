package com.example.kstream.core.utils;


import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

@Slf4j
public class JsonUtil {

    private JsonUtil() {}


    public static JSONObject fromJSONObject(String str)
    {
        try {
            return new JSONObject(str);
        } catch (JSONException e) {
            log.error(Throwables.getStackTraceAsString(e));
            return new JSONObject();
        }
    }


    public static Optional<JSONObject> getJSONObject(JSONObject json, String key) {
        JSONObject jsonObject = null;
        if(hasKeyAndNotNullValue(json, key)) {
            try {
                Object object = json.get(key);
                if(object instanceof JSONObject) {
                    jsonObject = (JSONObject) object;
                }
                else {
                    log.warn("JSONObject[" + JSONObject.quote(key) + "] is not a JSONObject.");
                }
            }
            catch (Exception ex) {
                log.warn(ex.getMessage());
            }
        }

        return Optional.ofNullable(jsonObject);
    }


    private static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }


    private static boolean hasKeyAndNotNullValue(JSONObject json, String key) {
        return json.has(key) && !json.isNull(key);
    }


    public static Optional<String> getString(JSONObject json, String key) {
        String str = null;
        if(hasKeyAndNotNullValue(json, key)) {
            try {
                Object object = json.get(key);
                if(object instanceof String) {
                    str = (String) object;
                }
                else {
                    str = object.toString();
                }
            } catch (Exception ex) {
                log.warn(ex.getMessage());
            }
        }
        return Optional.ofNullable(str);
    }


    public static Optional<String> getString(JSONObject json, String key1, String key2) {
        return getJSONObject(json, key1).flatMap(j -> getString(j, key2));
    }


    public static Optional<Long> getLong(JSONObject json, String key) {
        Long l = null;
        if(hasKeyAndNotNullValue(json, key)) {
            try {
                Object object = json.get(key);
                l = object instanceof Number ? ((Number)object).longValue() : Long.parseLong(String.valueOf(object));
            }
            catch (Exception ex) {
                log.warn(ex.getMessage());
            }
        }
        return Optional.ofNullable(l);
    }

    public static Optional<Integer> getInteger(JSONObject json, String key) {
        Integer i = null;
        if(hasKeyAndNotNullValue(json, key)) {
            try {
                Object obj = json.get(key);
                i = obj instanceof Number ? ((Number)obj).intValue() : Integer.parseInt(String.valueOf(obj));
            }
            catch (Exception ex) {
                log.warn(ex.getMessage());
            }
        }
        return Optional.ofNullable(i);
    }


    public static Optional<Double> getDouble(JSONObject json, String key) {
        Double val = null;
        if(hasKeyAndNotNullValue(json, key)) {
            try {
                Object obj = json.get(key);
                val = obj instanceof Number ? ((Number)obj).intValue() : Double.parseDouble(String.valueOf(obj));
            }
            catch (Exception ex) {
                log.warn(ex.getMessage());
            }
        }
        return Optional.ofNullable(val);
    }


    public static Optional<JSONArray> getJsonArray(JSONObject json, String key) {
        JSONArray arr = null;
        if(hasKeyAndNotNullValue(json, key)) {
            try {
                arr = json.getJSONArray(key);
            }
            catch (Exception ex) {
                log.warn(ex.getMessage());
            }
        }
        return Optional.ofNullable(arr);
    }

}
