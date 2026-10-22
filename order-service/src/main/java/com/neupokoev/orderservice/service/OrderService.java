package com.neupokoev.orderservice.service;

import com.neupokoev.orderservice.domain.dto.OrderCreateEditDto;
import com.neupokoev.orderservice.domain.dto.OrderReadDto;

public interface OrderService {

    OrderReadDto findOrderById(Long id);

    OrderReadDto createOrder(OrderCreateEditDto orderDto);

    void completeOrder(Long id);

    void cancelOrder(Long id, String reason);
}
