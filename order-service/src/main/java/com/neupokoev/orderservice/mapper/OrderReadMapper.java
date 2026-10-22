package com.neupokoev.orderservice.mapper;

import com.neupokoev.orderservice.domain.dto.OrderReadDto;
import com.neupokoev.orderservice.domain.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderReadMapper implements Mapper<Order, OrderReadDto> {

    @Override
    public OrderReadDto map(Order order) {
        OrderReadDto orderDto = new OrderReadDto(
                order.getId(),
                order.getDescription(),
                order.getCreateAt(),
                order.getPrice(),
                order.getStatus()
        );
        return orderDto;
    }
}
