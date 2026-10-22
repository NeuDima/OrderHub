package com.neupokoev.orderservice.outbox;

import com.neupokoev.contracts.order.events.OrderCreateEvent;
import com.neupokoev.orderservice.mapper.Mapper;
import org.springframework.stereotype.Component;

@Component
public class OutboxMapper implements Mapper<OrderCreateEvent, OutboxEvent> {

    @Override
    public OutboxEvent map(OrderCreateEvent orderCreateEvent) {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType("order")
                .aggregateId(orderCreateEvent.orderId().toString())
                .eventType("OrderCreateEvent")
                .payload(orderCreateEvent)
                .status(OutboxStatus.NEW)
                .build();
        return outboxEvent;
    }
}
