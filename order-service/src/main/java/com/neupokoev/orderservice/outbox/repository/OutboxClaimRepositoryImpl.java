package com.neupokoev.orderservice.outbox.repository;

import com.neupokoev.orderservice.outbox.OutboxEvent;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OutboxClaimRepositoryImpl implements OutboxClaimRepository {

    private final EntityManager entityManager;

    public List<OutboxEvent> claim(int limit, UUID claimToken) {
        String sql = """
            UPDATE outbox_event
            SET status = 'PROCESSING',
                claim_token = :claimToken,
                claimed_until = now() + interval '1 minutes',
                attempts = attempts + 1
            WHERE id IN (
                SELECT id
                FROM outbox_event
                WHERE status = 'NEW'
                   OR (
                       status = 'PROCESSING'
                       AND claimed_until < now()
                   )
                ORDER BY created_at, id
                LIMIT :limit
                FOR UPDATE SKIP LOCKED
            )
            RETURNING *
            """;

        @SuppressWarnings("unchecked")
        List<OutboxEvent> events = entityManager
                .createNativeQuery(sql, OutboxEvent.class)
                .setParameter("claimToken", claimToken)
                .setParameter("limit", limit)
                .getResultList();

        return events;
    }
}
