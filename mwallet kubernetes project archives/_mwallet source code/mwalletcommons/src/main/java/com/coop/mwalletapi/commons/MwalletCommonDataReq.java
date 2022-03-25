package com.coop.mwalletapi.commons;

import com.coop.mwalletapi.kafka.KafkaConfigs;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class MwalletCommonDataReq {
    DataSource mwalletDataSource;
    Map<String, String> commonDataMap;
    Object inObj;
    List<ApiUsers> apiUsers;
    KafkaConfigs kafkaConfigs;
}
