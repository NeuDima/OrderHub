package com.neupokoev.orderservice.outbox.repository;

import com.neupokoev.orderservice.outbox.OutboxEvent;

import java.util.List;
import java.util.UUID;

public interface OutboxClaimRepository {

    List<OutboxEvent> claim(int limit, UUID claimToken);
}
