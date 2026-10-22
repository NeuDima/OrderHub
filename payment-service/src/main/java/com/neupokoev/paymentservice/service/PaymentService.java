package com.neupokoev.paymentservice.service;

import com.neupokoev.contracts.payment.commands.ProcessPaymentCommand;

public interface PaymentService {

    void handlePaidOrder(ProcessPaymentCommand event);
}
