package com.example.kstream.core.model.dto;

import com.example.kstream.core.utils.JsonUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class TraceLog implements Serializable {

    private static final long LOG_CHECK_START_MS = 1000 * 5L;
    private static final long LOG_CHECK_INTERVAL_MS = 1000 * 3L;
    private static final long EVICTION_TIME_MS = 1000 * 30L;
    private static final short MAX_CHECK_COUNT = 9;

    private final long evictionDatetime;
    private long checkDatetime;

    private int checkCount = 0;

    private final String traceId;
    private final List<EventLog> eventLogs;

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
                    .withZone(ZoneId.systemDefault());

    public TraceLog(String traceId, List<EventLog> eventLogs) {
        this.traceId = traceId;
        this.eventLogs = eventLogs;
        this.evictionDatetime = 0L;
    }

    public TraceLog(String traceId, EventLog eventLog) {
        this.traceId = traceId;
        this.eventLogs = new ArrayList<>();
        this.eventLogs.add(eventLog);
        this.evictionDatetime = System.currentTimeMillis() + EVICTION_TIME_MS;
        this.checkDatetime = System.currentTimeMillis() + LOG_CHECK_START_MS;
        log.info(
                "Created tracelog: {}, eviction: {}, next check: {}",
                traceId, formatter.format(Instant.ofEpochMilli(evictionDatetime)),
                formatter.format(Instant.ofEpochMilli(checkDatetime))
        );
    }


    private Optional<EventLog> findReqLog(EventLog resLog) {
        return this.eventLogs.stream()
                .filter(l -> LogType.IN_REQ.getTypeString().equals(l.getLogType()))
                .filter(l -> l.equals(resLog))
                .findFirst();
    }


    public void addLog(EventLog eventLog) {
        if (this.traceId.equals(eventLog.getTraceId())) {
            if (LogType.IN_RES.getTypeString().equals(eventLog.getLogType())) {
                findReqLog(eventLog)
                        .ifPresent(reqLog -> eventLog.setReqLog(reqLog));
            }
            this.eventLogs.add(eventLog);
        }
    }


    public List<EventLog> getEventLogs() {
        return new CopyOnWriteArrayList<>(eventLogs);
    }


    public boolean isEmpty() {
        return this.eventLogs == null || eventLogs.isEmpty();
    }


    public Integer size() {
        if (this.isEmpty()) {
            return 0;
        }
        return this.eventLogs.size();
    }


    public void incrementCheckCount() {
        this.checkCount++;
    }

    public boolean maxCheckCount() {
        return MAX_CHECK_COUNT <= checkCount;
    }

    public boolean afterEvictionDatetime(long now) {
        return this.evictionDatetime <= now;
    }

    public boolean beforeCheckDatetime(long now) {
        return now < this.checkDatetime;
    }

    public void nextCheckDatetime() {
        this.checkDatetime = System.currentTimeMillis() + LOG_CHECK_INTERVAL_MS;
        log.debug("TraceLog current check count: {}, next check datetime: {}", this.checkCount, formatter.format(Instant.ofEpochMilli(checkDatetime)));
    }


    public boolean isValid() {
        long count = this.eventLogs.stream()
                .filter(l -> LogType.IN_RES.getTypeString().equals(l.getLogType()))
                .filter(l -> l.getReqLog() == null)
                .count();
        return count == 0;
    }


    public boolean checkTraceLog() {
        int inReqCnt = 0;
        int inResCnt = 0;

        for (EventLog log : eventLogs) {
            inReqCnt += LogType.IN_REQ.getTypeString().equals(log.getLogType()) ? 1 : 0;
            inResCnt += LogType.IN_RES.getTypeString().equals(log.getLogType()) ? 1 : 0;
        }

        if (inReqCnt == inResCnt && inResCnt > 0) {
            int logSize = this.eventLogs.size();
            EventLog firstLog = this.eventLogs.get(0);
            EventLog lastLog = this.eventLogs.get(logSize - 1);
            return firstLog.equals(lastLog);
        } else {
            return false;
        }
    }


    public JSONObject toJSONObject() {
        List<JSONObject> list = eventLogs.stream()
                .map(EventLog::toJsonObject)
                .collect(Collectors.toList());
        JSONObject json = new JSONObject();
        json.put("trace_id", this.getTraceId());
        json.put("logs", new JSONArray(list));
        return json;
    }


    public String toJsonString() {
        return toJSONObject().toString();
    }


    public static TraceLog from(JSONObject json) {
        String traceId = JsonUtil.getString(json, "trace_id").orElse("");
        if (json.has("logs")) {
            Optional<JSONArray> logs = JsonUtil.getJsonArray(json, "logs");
            List<EventLog> eventLogs = logs.map(ja -> {
                        List<EventLog> results = new ArrayList<>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jsonLogItem = ja.getJSONObject(i);
                            results.add(EventLog.from(jsonLogItem));
                        }
                        return results;
                    })
                    .orElse(new ArrayList<>());
            return new TraceLog(traceId, eventLogs);
        }
        return null;
    }
}
