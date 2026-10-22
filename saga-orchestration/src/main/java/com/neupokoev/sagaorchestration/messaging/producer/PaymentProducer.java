package com.neupokoev.sagaorchestration.messaging.producer;

import com.neupokoev.contracts.payment.commands.ProcessPaymentCommand;
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

    @Value("${app.topic.payment-command}")
    private String paymentEventsTopic;

    private final KafkaTemplate<String, Object> reliableKafkaTemplate;

    public void sendPayOrderCommand(ProcessPaymentCommand command) {

        String key = String.valueOf(command.orderId());

        CompletableFuture<SendResult<String, Object>> future =
                reliableKafkaTemplate.send(paymentEventsTopic, key, command);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Событие успешно отправлено в топик: {}, partition: {}, offset: {}, timestamp: {}",
                        paymentEventsTopic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().timestamp());
            } else {
                log.error("Ошибка отправки события в кафка по orderId: {}", command.orderId(), ex);
            }
        });
    }
}
