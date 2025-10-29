package org.example.emailnotificationmicroservice.handler;

import lombok.RequiredArgsConstructor;
import org.example.emailnotificationmicroservice.entity.ProcessedEvent;
import org.example.emailnotificationmicroservice.exception.NotRetryableException;
import org.example.emailnotificationmicroservice.exception.RetryableException;
import org.example.emailnotificationmicroservice.repository.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@KafkaListener(topics = "product-created-events-topic")
@RequiredArgsConstructor
public class ProductCreatedEventHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final RestTemplate restTemplate;
    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent event,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {

        ProcessedEvent existingRecord = processedEventRepository.findByMessageId(messageId);
        if (existingRecord != null) {
            LOGGER.info("found a dublicate message id: " + existingRecord.getMessageId());
            return;
        }

        LOGGER.info("Received a new event:" + event.getTitle());

        String requestUrl = "http://localhost:8082/response/200";
//        String requestUrl = "http://localhost:8082/response/500";

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);
            if(response.getStatusCode().value() == HttpStatus.OK.value()){
                LOGGER.info("received response from a remote service: " + response.getBody());
            }
        }catch (ResourceAccessException ex){
            LOGGER.error(ex.getMessage());
            throw new RetryableException(ex);
        }catch (Exception ex){
            LOGGER.error(ex.getMessage());
            throw new NotRetryableException(ex);
        }

        try {
            processedEventRepository.save(new ProcessedEvent( messageId, event.getId()));
        }catch (DataIntegrityViolationException exception){
            throw new NotRetryableException(exception);
        }
    }
}
