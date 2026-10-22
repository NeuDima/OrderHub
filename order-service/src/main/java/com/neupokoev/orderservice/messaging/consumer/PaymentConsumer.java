package com.neupokoev.orderservice.messaging.consumer;

import com.neupokoev.contracts.order.commands.MarkOrderFailedCommand;
import com.neupokoev.contracts.order.commands.MarkOrderPaidCommand;
import com.neupokoev.orderservice.service.OrderService;
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

    private final OrderService orderService;

    @KafkaListener(
            topics = "${app.topic.order-command-topic}",
            groupId = "order-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentEvent(
            ConsumerRecord<String, Object> record,
            Acknowledgment acknowledgment) {

        Object event = record.value();

        try {
            if (event instanceof MarkOrderPaidCommand completed) {
                orderService.completeOrder(completed.orderId());

            } else if (event instanceof MarkOrderFailedCommand failed) {
                orderService.cancelOrder(failed.orderId(), failed.reason());

            } else {
                throw new IllegalArgumentException("Unsupported payment event: " + event.getClass().getName());
            }

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Ошибка обработки события оплаты", e);
            throw new RuntimeException("Payment event processing failed", e);
        }
    }
}
