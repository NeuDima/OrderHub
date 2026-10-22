package com.neupokoev.contracts.payment.events;

import com.neupokoev.common.OrderStatusEvent;

import java.util.UUID;

public record PaymentFailedEvent (
        UUID commandId,
        UUID sagaId,
        Long orderId,
        OrderStatusEvent status,
        String reason
) {
}