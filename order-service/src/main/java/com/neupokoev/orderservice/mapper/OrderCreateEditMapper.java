package com.neupokoev.orderservice.mapper;

import com.neupokoev.orderservice.domain.dto.OrderCreateEditDto;
import com.neupokoev.orderservice.domain.entity.Order;
import com.neupokoev.orderservice.domain.entity.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OrderCreateEditMapper implements Mapper<OrderCreateEditDto, Order> {

    @Override
    public Order map(OrderCreateEditDto orderDto) {
        Order order = Order.builder()
                .description(orderDto.description())
                .createAt(Instant.now())
                .price(orderDto.price())
                .status(OrderStatus.CREATE)
                .build();
        return order;
    }
}
