package com.at.t.ecommerce.inventory.infrastructure.messaging.kafka;

import com.at.t.ecommerce.inventory.domain.stock.events.StockEventPublisher;
import com.at.t.ecommerce.inventory.domain.stock.events.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaStockEventPublisherAdapter implements StockEventPublisher {

    // Spring Boot automatically configures this based on your application-dev.yml
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    // The APEX Notification Service will be listening to this exact topic
    private static final String TOPIC = "spm-inventory-events";

    @Override
    public void publishStockReservedEvent(StockReservedEvent event) {
        log.info("📡 Broadcasting StockReservedEvent to Kafka topic [{}]: {}", TOPIC, event.eventId());
        
        // We use the productId as the Kafka Message Key to guarantee ordering
        kafkaTemplate.send(TOPIC, event.productId(), event);
    }
}