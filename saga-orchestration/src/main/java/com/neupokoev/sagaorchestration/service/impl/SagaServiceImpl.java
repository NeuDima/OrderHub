package com.neupokoev.sagaorchestration.service.impl;

import com.neupokoev.contracts.order.commands.MarkOrderFailedCommand;
import com.neupokoev.contracts.order.commands.MarkOrderPaidCommand;
import com.neupokoev.contracts.order.events.OrderCreateEvent;
import com.neupokoev.contracts.payment.commands.ProcessPaymentCommand;
import com.neupokoev.contracts.payment.events.PaymentCompletedEvent;
import com.neupokoev.contracts.payment.events.PaymentFailedEvent;
import com.neupokoev.sagaorchestration.messaging.producer.OrderProducer;
import com.neupokoev.sagaorchestration.messaging.producer.PaymentProducer;
import com.neupokoev.sagaorchestration.service.SagaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaServiceImpl implements SagaService {

    private final PaymentProducer paymentProducer;
    private final OrderProducer orderProducer;

    @Override
    public void handleOrderCreated(OrderCreateEvent orderCreateEvent) {
        log.info("В handleOrderCreated пришло событие OrderCreateEvent");
        ProcessPaymentCommand paymentCommand = new ProcessPaymentCommand(
                UUID.randomUUID(),
                orderCreateEvent.sagaId(),
                orderCreateEvent.orderId(),
                orderCreateEvent.price()
        );
        paymentProducer.sendPayOrderCommand(paymentCommand);
        log.info("handleOrderCreated отправил команду ProcessPaymentCommand");
    }

    @Override
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("В handlePaymentCompleted пришло событие PaymentCompletedEvent");
        MarkOrderPaidCommand command = new MarkOrderPaidCommand(
                UUID.randomUUID(),
                event.sagaId(),
                event.orderId(),
                event.status()
        );
        orderProducer.sendChangeStatusCommand(event.orderId(), command);
        log.info("handlePaymentCompleted отправил команду MarkOrderPaidCommand");
    }

    @Override
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("В handlePaymentFailed пришло событие PaymentFailedEvent");
        MarkOrderFailedCommand command = new MarkOrderFailedCommand(
                UUID.randomUUID(),
                event.sagaId(),
                event.orderId(),
                event.status(),
                event.reason()
        );
        orderProducer.sendChangeStatusCommand(event.orderId(), command);
        log.info("handlePaymentFailed отправил команду MarkOrderFailedCommand");
    }
}
