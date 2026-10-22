package com.neupokoev.sagaorchestration.messaging.producer;

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
public class OrderProducer {

    @Value("${app.topic.order-command-topic}")
    private String orderCreateTopic;

    private final KafkaTemplate<String, Object> reliableKafkaTemplate;

    public void sendChangeStatusCommand(Long orderId, Object command) {
        String key = String.valueOf(orderId);

        CompletableFuture<SendResult<String, Object>> future =
                reliableKafkaTemplate.send(orderCreateTopic, key, command);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Событие успешно отправлено в топик: {}, partition: {}, offset: {}, timestamp: {}",
                        orderCreateTopic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().timestamp());
            } else {
                log.error("Ошибка отправки события в кафка по orderId: {}", orderId, ex);
            }
        });
    }
}
