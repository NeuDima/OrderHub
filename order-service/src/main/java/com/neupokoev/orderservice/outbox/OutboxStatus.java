package com.neupokoev.orderservice.outbox;

public enum OutboxStatus {
    NEW,
    PROCESSING,
    PUBLISHED,
    FAILED
}
