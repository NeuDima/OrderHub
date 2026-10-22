package com.neupokoev.orderservice.controller;

import com.neupokoev.orderservice.domain.dto.OrderCreateEditDto;
import com.neupokoev.orderservice.domain.dto.OrderReadDto;
import com.neupokoev.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderReadDto> findOrder(@PathVariable Long id) {

        OrderReadDto orderDto = orderService.findOrderById(id);
        return ResponseEntity.ok(orderDto);
    }

    @PostMapping
    public ResponseEntity<OrderReadDto> createOrder(@RequestBody OrderCreateEditDto orderDto) {

        OrderReadDto orderReadDto = orderService.createOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderReadDto);
    }
}
