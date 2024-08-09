package com.appsdeveloperblog.emailnotification.service.handler;


import com.appsdeveloperblog.emailnotification.exception.NotRetryableException;
import com.appsdeveloperblog.emailnotification.exception.RetryableException;
import com.appsdeveloperblog.model.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {

    private final RestTemplate restTemplate;

    @KafkaHandler
    public void handle(ProductCreatedEvent event) {
        log.info("\u001B[32m ********* Received a new event: {} \u001B[0m", event.getTitle());
        String requestUrl = "http://localhost:8082";
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUrl, HttpMethod.GET,null, String.class);
            if(response.getStatusCode().equals(HttpStatus.OK)) {
                log.info("\u001B[32m ********* Received response from remote service: {} \u001B[0m", response.getBody());
            }
        }catch (ResourceAccessException e) {
            log.error(e.getLocalizedMessage());
            throw new RetryableException(e.getLocalizedMessage());
        }catch (HttpServerErrorException e) {
            log.error(e.getLocalizedMessage());
            throw new NotRetryableException(e.getLocalizedMessage());
        }catch (Exception e) {
            throw new NotRetryableException(e.getLocalizedMessage());
        }
    }
}
