package com.appsdeveloperblog.products.service.impl;

import com.appsdeveloperblog.products.model.Product;
import com.appsdeveloperblog.products.model.ProductCreatedEvent;
import com.appsdeveloperblog.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public String createProduct(Product product) {

        String productId = UUID.randomUUID().toString();

        // TODO: Persist Product Details into database table before publishing an Event

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                productId, product.getTitle(), product.getPrice(), product.getQuantity()
        );

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent);

        future.whenComplete((result, exception)->{
            if(exception != null) {
                log.error("\u001B[31m ********** Failed to send massage: {} \u001B[0m", exception.getMessage());
            }else {
                log.info("\u001B[32m ********** Successfully sent massage: {} \u001B[0m", result.getRecordMetadata().topic());
                log.info("\u001B[32m ********** Successfully sent offset: {} \u001B[0m", result.getRecordMetadata().offset());
                log.info("\u001B[32m ********** Successfully sent partition: {} \u001B[0m", result.getRecordMetadata().partition());
            }
        });

        future.join();

        log.info("\u001B[32m ********** Returning product id: {} \u001B[0m", productId);
        // this make process as sync operation and wait until get result otherwise send exception
//        future.join();
        return productId;
    }
}
