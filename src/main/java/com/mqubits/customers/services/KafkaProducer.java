package com.mqubits.customers.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    public void send(String topic, Object payload) {
        LOGGER.info("sending payload='{}' to topic='{}'", payload, topic);
        var result = kafkaTemplate.send(topic, payload);
    }
}
