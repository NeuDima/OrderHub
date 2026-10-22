package com.neupokoev.contracts.order.commands;

import com.neupokoev.common.OrderStatusEvent;

import java.util.UUID;

public record MarkOrderFailedCommand(
        UUID commandId,
        UUID sagaId,
        Long orderId,
        OrderStatusEvent status,
        String reason
) {
}