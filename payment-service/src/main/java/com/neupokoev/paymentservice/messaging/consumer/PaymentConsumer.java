package com.neupokoev.paymentservice.messaging.consumer;

import com.neupokoev.contracts.payment.commands.ProcessPaymentCommand;
import com.neupokoev.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "${app.topic.payment-command}",
            groupId = "payment-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentEvent(
            ConsumerRecord<String, Object> record,
            Acknowledgment acknowledgment) {

        Object event = record.value();

        try {
            if (event instanceof ProcessPaymentCommand command) {
                paymentService.handlePaidOrder(command);

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
