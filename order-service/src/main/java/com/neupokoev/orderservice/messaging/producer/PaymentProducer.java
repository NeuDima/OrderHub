package com.neupokoev.orderservice.messaging.producer;

import com.neupokoev.contracts.order.events.OrderCreateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    @Value("${app.topic.order-create-event-topic}")
    private String orderCreateTopic;

    private final KafkaTemplate<String, Object> reliableKafkaTemplate;


    public CompletableFuture<SendResult<String, Object>> sendOrderCreateEvent(OrderCreateEvent event) {

        String key = String.valueOf(event.orderId());

        CompletableFuture<SendResult<String, Object>> future =
                reliableKafkaTemplate.send(orderCreateTopic, key, event);

//        future.whenComplete((result, ex) -> {
//            if (ex == null) {
//                log.info("Событие успешно отправлено в топик: {}, partition: {}, offset: {}, timestamp: {}",
//                        orderCreateTopic,
//                        result.getRecordMetadata().partition(),
//                        result.getRecordMetadata().offset(),
//                        result.getRecordMetadata().timestamp());
//            } else {
//                log.error("Ошибка отправки события в кафка по orderId: {}", event.orderId(), ex);
//            }
//        });

        return future;
    }
}
