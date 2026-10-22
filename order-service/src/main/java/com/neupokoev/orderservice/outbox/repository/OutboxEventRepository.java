package com.neupokoev.orderservice.outbox.repository;

import com.neupokoev.orderservice.outbox.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;


public interface OutboxEventRepository extends
        OutboxClaimRepository,
        JpaRepository<OutboxEvent, Long> {


    @Modifying
    @Query(value = """
        UPDATE outbox_event
        SET status = 'PUBLISHED',
            processed_at = now(),
            claimed_until = NULL,
            claim_token = NULL
        WHERE id = :id
          AND status = 'PROCESSING'
          AND claim_token = :claimToken
        """, nativeQuery = true)
    int markPublished(@Param("id") Long id, @Param("claimToken") UUID claimToken);


    @Modifying
    @Query(value = """
        UPDATE outbox_event
        SET status = CASE
                WHEN attempts >= :maxAttempts THEN 'FAILED'
                ELSE 'NEW'
            END,
            claimed_until = NULL,
            claim_token = NULL
        WHERE id = :id
          AND status = 'PROCESSING'
          AND claim_token = :claimToken
        """, nativeQuery = true)
    int markFailed(
            @Param("id") Long id,
            @Param("claimToken") UUID claimToken,
            @Param("maxAttempts") int maxAttempts
    );
}
