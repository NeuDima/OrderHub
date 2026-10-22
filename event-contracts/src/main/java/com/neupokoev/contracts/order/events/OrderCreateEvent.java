package com.neupokoev.contracts.order.events;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreateEvent(
        UUID eventId,
        UUID sagaId,
        Long orderId,
        BigDecimal price
) {
}
