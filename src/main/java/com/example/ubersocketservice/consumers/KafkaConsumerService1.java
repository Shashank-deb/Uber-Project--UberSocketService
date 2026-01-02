package com.example.ubersocketservice.consumers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService1 {


    @KafkaListener(topics = "sample-topic",groupId = "my-group3")
    public void listen(String message) {
        System.out.println("Kafka message from sample topic  inside socket service: " + message);
    }


}
