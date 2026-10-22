create table outbox_event
(
    id             SERIAL PRIMARY KEY,
    aggregate_type TEXT NOT NULL,
    aggregate_id   TEXT NOT NULL,
    event_type     TEXT NOT NULL,
    payload        JSON NOT NULL,
    status         TEXT CHECK ( status IN ('NEW', 'PROCESSING', 'PUBLISHED', 'FAILED')),
    attempts       INT,
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    processed_at   TIMESTAMP WITH TIME ZONE,
    claim_token    UUID,
    claimed_until  TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_outbox_event_status_created_at ON outbox_event (status, created_at);