package com.coop.mwalletapi.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author okahia
 */

public class KafkaLogProducer {

    Producer<String, String> KafkaProducer;
    
    private static final Logger logger = LogManager.getLogger(KafkaLogProducer.class.getName());
    
    public KafkaLogProducer(String Broker, String ClientId, String Topic) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Broker);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, ClientId);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer = new KafkaProducer<>(props);
    }

    public void sendMessage(String CallingClass, String Topic, String Key, Map<String, String> messageMap) {
        try {
            long time = System.currentTimeMillis();
            String message = new ObjectMapper().writeValueAsString(messageMap);

            if ("".equalsIgnoreCase(message) || "{}".equalsIgnoreCase(message)) {
                logger.info("KAFKA : Calling Class : " + CallingClass + " Message : " + message + " Topic : " + Topic + " Key : " + Key, "");
            } else {
                try {
                    final ProducerRecord<String, String> record = new ProducerRecord<>(Topic, Key, message);
                    KafkaProducer.send(record, (metadata, exception) -> {
                        long elapsedTime = System.currentTimeMillis() - time;
                        if (metadata != null) {
                            System.out.printf("sent record(key=%s value=%s) meta(partition=%d, offset=%d) time=%d\n",
                                    record.key(), record.value(), metadata.partition(), metadata.offset(), elapsedTime);
                        } else {
                            exception.printStackTrace();
                        }
                    });
                } finally {
                    KafkaProducer.flush();
                    KafkaProducer.close();
                }
            }

        } catch (Exception ex) {
            logger.error(KafkaLogProducer.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
            KafkaProducer.close();
        }
    }

}
