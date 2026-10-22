package com.neupokoev.orderservice.outbox.service.impl;

import com.neupokoev.orderservice.outbox.OutboxEvent;
import com.neupokoev.orderservice.outbox.repository.OutboxEventRepository;
import com.neupokoev.orderservice.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxServiceImpl implements OutboxService {

    private final OutboxEventRepository outboxEventRepository;

    @Override
    @Transactional
    public List<OutboxEvent> claimBatch(int limit, UUID claimToken) {
        return outboxEventRepository.claim(limit, claimToken);
    }

    @Override
    @Transactional
    public void markPublished(Long id, UUID claimToken) {
        int updated = outboxEventRepository.markPublished(id, claimToken);

        if (updated == 0) {
            log.warn("Could not mark outbox event {} as PUBLISHED: claim was lost", id);
        }
    }

    @Override
    @Transactional
    public void markFailed(Long id, UUID claimToken, int maxAttempts) {

        outboxEventRepository.markFailed(id, claimToken, maxAttempts);
    }
}
