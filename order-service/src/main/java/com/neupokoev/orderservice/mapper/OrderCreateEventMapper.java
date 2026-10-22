package com.neupokoev.orderservice.mapper;

import com.neupokoev.contracts.order.events.OrderCreateEvent;
import com.neupokoev.orderservice.domain.entity.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderCreateEventMapper implements Mapper<Order, OrderCreateEvent> {

    @Override
    public OrderCreateEvent map(Order order) {
        OrderCreateEvent orderCreateEvent = new OrderCreateEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                order.getId(),
                order.getPrice()
        );
        return orderCreateEvent;
    }
}
