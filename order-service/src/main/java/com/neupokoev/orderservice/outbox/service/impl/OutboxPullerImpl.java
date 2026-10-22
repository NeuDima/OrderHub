package com.neupokoev.orderservice.outbox.service.impl;

import com.neupokoev.orderservice.messaging.producer.PaymentProducer;
import com.neupokoev.orderservice.outbox.OutboxEvent;
import com.neupokoev.orderservice.outbox.service.OutboxPuller;
import com.neupokoev.orderservice.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPullerImpl implements OutboxPuller {

    private static final int LIMIT = 100;
    private static final int MAX_ATTEMPTS = 10;
    private static final long SEND_TIMEOUT_SECONDS = 30;

    private final PaymentProducer paymentProducer;
    private final OutboxService outboxService;


    @Override
    @Scheduled(fixedDelay = 1_000)
    public void poll() {
        UUID claimToken = UUID.randomUUID();

        List<OutboxEvent> events = outboxService.claimBatch(LIMIT, claimToken);

        if (events.isEmpty()) {
            return;
        }

        log.info("Количество outbox events для отправки: {}", events.size());

        for (OutboxEvent event : events) {
            publish(event, claimToken);
        }
    }

    private void publish(OutboxEvent event, UUID claimToken) {
        try {
            paymentProducer.sendOrderCreateEvent(event.getPayload())
                    .get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            outboxService.markPublished(event.getId(), claimToken);

            log.info("Outbox event {} обновлён для заказа: {}", event.getId(), event.getAggregateId());

        } catch (Exception e) {
            log.error("Не получислось обновить outbox event {}", event.getId(), e);

            try {
                outboxService.markFailed(event.getId(), claimToken, MAX_ATTEMPTS);
            } catch (Exception statusException) {
                log.error("Не получилось обновить статус для OutboxEvent: {}", event.getId(), statusException);
            }
        }
    }
}
