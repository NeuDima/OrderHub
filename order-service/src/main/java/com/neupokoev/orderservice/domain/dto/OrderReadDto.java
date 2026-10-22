package com.neupokoev.orderservice.domain.dto;

import com.neupokoev.orderservice.domain.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderReadDto(
        Long id,
        String description,
        Instant createAt,
        BigDecimal price,
        OrderStatus status
) {}
