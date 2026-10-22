package com.neupokoev.orderservice.service.impl;

import com.neupokoev.contracts.order.events.OrderCreateEvent;
import com.neupokoev.orderservice.domain.dto.OrderCreateEditDto;
import com.neupokoev.orderservice.domain.dto.OrderReadDto;
import com.neupokoev.orderservice.domain.entity.Order;
import com.neupokoev.orderservice.domain.entity.enums.OrderStatus;
import com.neupokoev.orderservice.mapper.OrderCreateEditMapper;
import com.neupokoev.orderservice.mapper.OrderCreateEventMapper;
import com.neupokoev.orderservice.mapper.OrderReadMapper;
import com.neupokoev.orderservice.messaging.producer.PaymentProducer;
import com.neupokoev.orderservice.outbox.OutboxEvent;
import com.neupokoev.orderservice.outbox.OutboxMapper;
import com.neupokoev.orderservice.outbox.repository.OutboxEventRepository;
import com.neupokoev.orderservice.repository.OrderRepository;
import com.neupokoev.orderservice.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderReadMapper orderReadMapper;
    private final OrderCreateEditMapper orderCreateEditMapper;
    private final PaymentProducer paymentProducer;
    private final OrderCreateEventMapper orderCreateEventMapper;

    private final OutboxMapper outboxMapper;
    private final OutboxEventRepository outboxEventRepository;

    @Override
    public OrderReadDto findOrderById(Long id) {
        log.info("В методе findOrderById получен запрос на поиск order по id: {}", id);
        return orderRepository.findById(id)
                .map(orderReadMapper::map)
                .orElseThrow(() -> new EntityNotFoundException("Не найден order с id: " + id));
    }

    @Override
    @Transactional
    public OrderReadDto createOrder(OrderCreateEditDto orderDto) {
        log.info("В методе createOrder пришёл запрос на создание order: {}", orderDto);

        Order order = Optional.ofNullable(orderDto)
                .map(orderCreateEditMapper::map)
                .map(orderRepository::saveAndFlush)
                .orElseThrow(() -> new RuntimeException("Не получилось создать order по dto: " + orderDto));

        // Пока что нет разделения статусов CREATE и WAITING для бизнес-логики;
        // CREATE сразу заменяется на WAITING
        order.setStatus(OrderStatus.WAITING);
        orderRepository.save(order);

        log.info("Заказ подгатовлен к отправке id: {} со статусом WAITING", order.getId());

        OrderCreateEvent orderCreateEvent = orderCreateEventMapper.map(order);

        OutboxEvent outboxEvent = outboxMapper.map(orderCreateEvent);
        outboxEventRepository.save(outboxEvent);

        return orderReadMapper.map(order);
    }

    @Override
    @Transactional
    public void completeOrder(Long id) {
        log.info("В методе completeOrder получен запрос на обновление статуса оплаты на PAID у order с id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден order с id: " + id));

        if (order.getStatus() != OrderStatus.WAITING) {
            log.info("Order {} is not WAITING state (current: {}), skipping", id, order.getStatus());
            return;
        }

        order.setStatus(OrderStatus.PAID);

        orderRepository.saveAndFlush(order);

        log.info("Статус order c id: {} был изменён на PAID", id);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id, String reason) {
        log.info("В методе cancelOrder получен запрос на обновление статуса оплаты на FAILED у order с id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не найден order с id: " + id));

        if (order.getStatus() != OrderStatus.WAITING) {
            log.info("Order {} is not WAITING state (current: {}), skipping", id, order.getStatus());
            return;
        }

        order.setStatus(OrderStatus.FAILED);

        orderRepository.saveAndFlush(order);

        log.info("Статус order c id: {} был изменён на FAILED", id);
    }
}
