package org.example.productmicroservice.service;

import lombok.RequiredArgsConstructor;
import org.example.productmicroservice.dto.CreateProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    //--- asynchronous method
    public String createProduct(CreateProductDto product) {

        // simulation of create product on db

        String productId = UUID.randomUUID().toString();
        ProductCreatedEvent event =
                new ProductCreatedEvent(
                        productId, product.getTitle(), product.getPrice(), product.getQuantity()
                );

        CompletableFuture<SendResult<String, ProductCreatedEvent>> future =
                kafkaTemplate.send("product-created-events-topic",productId, event);

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                LOGGER.error("********* Failed to sent message" + exception.getMessage(), exception);
            }else{
                LOGGER.info("********* Successfully sent message" + result.getRecordMetadata());
            }
        });

        //future.join(); --- this code make synchronous

        LOGGER.info("********* Returning product id");

        return productId;
    }

    //--- synchronous method
    public String createProduct2(CreateProductDto product) {

        // simulation of create product on db

        String productId = UUID.randomUUID().toString();
        ProductCreatedEvent event =
                new ProductCreatedEvent(
                        productId, product.getTitle(), product.getPrice(), product.getQuantity()
                );
        CompletableFuture<SendResult<String, ProductCreatedEvent>> future =
                kafkaTemplate.send("product-created-events-topic",productId, event);

        SendResult<String, ProductCreatedEvent> result =
                kafkaTemplate.send("product-created-events-topic",productId, event).join();

        LOGGER.info("********* Returning product id");

        return productId;
    }
}
