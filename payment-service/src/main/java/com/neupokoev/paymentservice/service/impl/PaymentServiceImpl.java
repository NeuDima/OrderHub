package com.neupokoev.paymentservice.service.impl;

import com.neupokoev.common.OrderStatusEvent;
import com.neupokoev.contracts.payment.commands.ProcessPaymentCommand;
import com.neupokoev.contracts.payment.events.PaymentCompletedEvent;
import com.neupokoev.contracts.payment.events.PaymentFailedEvent;
import com.neupokoev.paymentservice.domain.dictionary.PaymentStatus;
import com.neupokoev.paymentservice.domain.entity.Payment;
import com.neupokoev.paymentservice.messaging.producer.PaymentProducer;
import com.neupokoev.paymentservice.repository.PaymentRepository;
import com.neupokoev.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentProducer paymentProducer;
    private final PaymentRepository paymentRepository;
    private final Random random = new Random(10);
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void handlePaidOrder(ProcessPaymentCommand command) {

        String lockKey = "payment:order:" + command.orderId();

        try {
            // Проверка через redis
            Boolean locked = stringRedisTemplate.opsForValue()
                    .setIfAbsent(
                            lockKey,
                            "processing",
                            Duration.ofHours(24)
                    );

            log.info("Redis обработал блокировку order: {}, результат: {}", command.orderId(), locked);

            if (Boolean.FALSE.equals(locked)) {
                log.info("Заказ {} уже обрабатывается или обработан", command.orderId());
                return;
            }

            // Проверка через бд (inbox pattern)
            Optional<Payment> existing = paymentRepository.findByOrderId(command.orderId());
            if (existing.isPresent()) {
                log.info("Платёж уже обработан для заказа: {}", command.orderId());
                return;
            }

            String transactionId = "txn_" + System.currentTimeMillis();

            Payment payment = Payment.builder()
                    .orderId(command.orderId())
                    .price(command.price())
                    .transactionId(transactionId)
                    .status(PaymentStatus.COMPLETED)
                    .createdAt(ZonedDateTime.now())
                    .build();

            paymentRepository.saveAndFlush(payment);

            // TODO Какая-то логика оплаты
            int result = random.nextInt();

            if (result > 5) {
                PaymentCompletedEvent event = new PaymentCompletedEvent(
                        UUID.randomUUID(),
                        command.sagaId(),
                        command.orderId(),
                        OrderStatusEvent.PAID
                );
                paymentProducer.sendResultPayment(command.orderId(), event);
            } else {
                PaymentFailedEvent event = new PaymentFailedEvent(
                        UUID.randomUUID(),
                        command.sagaId(),
                        command.orderId(),
                        OrderStatusEvent.FAILED,
                        "Потому что так хочется)"
                );
                paymentProducer.sendResultPayment(command.orderId(), event);
            }
        } catch (Exception e) {
            stringRedisTemplate.delete(lockKey);
            throw new RuntimeException("Ошибка обработки платежа");
        }
    }
}
