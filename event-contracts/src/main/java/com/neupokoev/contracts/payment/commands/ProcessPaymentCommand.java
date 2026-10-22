package com.neupokoev.contracts.payment.commands;

import java.math.BigDecimal;
import java.util.UUID;

public record ProcessPaymentCommand(
        UUID commandId,
        UUID sagaId,
        Long orderId,
        BigDecimal price
) {
}
