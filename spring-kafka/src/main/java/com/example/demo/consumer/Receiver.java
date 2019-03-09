package com.example.demo.consumer;

import com.example.demo.data.Payload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;

@Slf4j
@Component
public class Receiver {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(groupId = "my-group", topics = "${kafka.topic}")
    public void receive(String payload) {
        try {
            Payload payload1 = objectMapper.readValue(payload, Payload.class);
            log.info("received payload=" + payload1);
            this.eventPublisher.publishEvent(payload1);
        } catch (IOException e) {
            log.error("received payload=" + payload);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
