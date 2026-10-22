package com.neupokoev.sagaorchestration.service;

import com.neupokoev.contracts.order.events.OrderCreateEvent;
import com.neupokoev.contracts.payment.events.PaymentCompletedEvent;
import com.neupokoev.contracts.payment.events.PaymentFailedEvent;

public interface SagaService {

    void handleOrderCreated(OrderCreateEvent orderCreateEvent);

    void handlePaymentCompleted(PaymentCompletedEvent event);

    void handlePaymentFailed(PaymentFailedEvent event);
}
