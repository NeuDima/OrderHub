package com.neupokoev.sagaorchestration.messaging.consumer;

import com.neupokoev.contracts.order.events.OrderCreateEvent;
import com.neupokoev.sagaorchestration.service.SagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    private final SagaService sagaService;

    @KafkaListener(
            topics = "${app.topic.order-create-event-topic}",
            groupId = "saga-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentEvent(
            ConsumerRecord<String, Object> record,
            Acknowledgment acknowledgment) {

        Object event = record.value();

        try {
            if (event instanceof OrderCreateEvent orderCreateEvent) {
                sagaService.handleOrderCreated(orderCreateEvent);

            } else {
                throw new IllegalArgumentException("Unsupported payment event: " + event.getClass().getName());
            }

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.info("Ошибка обработки события оплаты", e);
            throw new RuntimeException("Payment event processing failed", e);
        }
    }
}
