package com.neupokoev.orderservice.outbox.service;

import com.neupokoev.orderservice.outbox.OutboxEvent;

import java.util.List;
import java.util.UUID;

public interface OutboxService {

    List<OutboxEvent> claimBatch(int limit, UUID claimToken);

    void markPublished(Long id, UUID claimToken);

    void markFailed(Long id, UUID claimToken, int maxAttempts);
}
