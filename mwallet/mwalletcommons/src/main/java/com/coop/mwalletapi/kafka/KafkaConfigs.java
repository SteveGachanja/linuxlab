package com.coop.mwalletapi.kafka;

import lombok.Data;

/**
 *
 * @author okahia
 */
@Data
public class KafkaConfigs {
    String server;
    String topic;
    String clientId;
}
