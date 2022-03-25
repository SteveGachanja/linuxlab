package com.coop.mwalletapi.configs;

import com.coop.mwalletapi.kafka.KafkaConfigs;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author okahia
 */

@Configuration
public class LoadKafkaConfigs {

    @Bean
    @ConfigurationProperties(prefix = "kafka")
    public KafkaConfigs kafkaConfigs() {
        return new KafkaConfigs();
    }
}