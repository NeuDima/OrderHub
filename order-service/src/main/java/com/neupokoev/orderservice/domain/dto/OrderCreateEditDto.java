package com.neupokoev.orderservice.domain.dto;

import java.math.BigDecimal;

public record OrderCreateEditDto (
        String description,
        BigDecimal price
) {}
