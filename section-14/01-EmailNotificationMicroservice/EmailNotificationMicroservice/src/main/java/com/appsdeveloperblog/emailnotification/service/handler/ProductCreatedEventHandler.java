package com.appsdeveloperblog.emailnotification.service.handler;


import com.appsdeveloperblog.emailnotification.exception.NotRetryableException;
import com.appsdeveloperblog.emailnotification.exception.RetryableException;
import com.appsdeveloperblog.emailnotification.model.ProcessedEventEntity;
import com.appsdeveloperblog.emailnotification.repository.ProcessedEventRepository;
import com.appsdeveloperblog.model.ProductCreatedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
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

    private final ProcessedEventRepository processedEventRepository;



    @KafkaHandler
    @Transactional
    public void handle(
            @Payload ProductCreatedEvent event,
            @Header("messageId") String messageId,
            @Header(KafkaHeaders.RECEIVED_KEY) String messageKey
            ) {
        log.info("\u001B[32m ********* Received a new event: {} With Product ID: {} \u001B[0m",
                event.getTitle(), event.getProductId());

        // Check if this message was already processed before
        ProcessedEventEntity existingRecord = processedEventRepository.findByMessageId(messageId);

        if (existingRecord != null) {
            log.warn("Found a duplicate message id : {}", existingRecord.getMessageId());
            return;
        }

        String requestUrl = "http://localhost:8082/response/200";
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

        try {
            // save messageId from header to DB
            processedEventRepository.save(new ProcessedEventEntity(messageId, event.getProductId()));
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw new NotRetryableException(e.getLocalizedMessage());
        }
    }
}
