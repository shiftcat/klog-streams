package com.example.kstream.core.aggregator;

import com.example.kstream.core.model.dto.EventLog;
import com.example.kstream.core.model.dto.ServiceStatistics;
import com.example.kstream.core.model.dto.ServiceLog;
import com.example.kstream.core.model.vo.Response;
import org.apache.kafka.streams.kstream.Aggregator;

public class LogAggregator<T> implements Aggregator<T, ServiceLog, ServiceStatistics> {



    @Override
    public ServiceStatistics apply(T k, ServiceLog v, ServiceStatistics agg) {
        agg.setKey(k);
        if(v == null) {
            return agg;
        }

        agg.addServiceLog(v);
        EventLog resLog = v.getResLog();
        agg.incrementTotalCount();

        Response response = resLog.getResponse();

        Integer status = response.getStatus();
        if(status >= 200 && status < 300) {
            agg.incrementSuccessCount();
        }
        else {
            if(status >= 300 && status < 500) {
                agg.incrementUserErrorCount();
            }
            else {
                agg.incrementServerErrorCount();
            }
        }

        if(3000 < response.getDuration()) {
            agg.incrementOverThreeCount();
        } else if(1000 < response.getDuration()) {
            agg.incrementOverOneCount();
        }

        agg.addDuration(response.getDuration());
        agg.setMinDuration(response.getDuration());
        agg.setMaxDuration(response.getDuration());
        agg.calculateDurationAverage();
        return agg;
    }

}
