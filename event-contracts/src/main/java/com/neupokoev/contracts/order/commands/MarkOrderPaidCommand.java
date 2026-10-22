package com.neupokoev.contracts.order.commands;

import com.neupokoev.common.OrderStatusEvent;

import java.util.UUID;

public record MarkOrderPaidCommand(
        UUID eventId,
        UUID sagaId,
        Long orderId,
        OrderStatusEvent status
) {
}