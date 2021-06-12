package com.mqubits.customers.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class TestKafkaConsumer {
    protected static final Logger LOGGER = LoggerFactory.getLogger(TestMembershipKafkaConsumer.class);

    protected CountDownLatch latch = new CountDownLatch(1);
    protected String payload = null;

    public CountDownLatch getLatch() {
        return latch;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}
