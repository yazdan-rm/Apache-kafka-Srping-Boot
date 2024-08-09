package com.appsdeveloperblog.emailnotification.service.handler;


import com.appsdeveloperblog.model.ProductCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {

    @KafkaHandler
    public void handle(ProductCreatedEvent event) {
        log.info("\u001B[32m ********* Received a new event: {} \u001B[0m", event.getTitle());
    }
}
