create table payment
(
    id             serial primary key,
    order_id       BIGINT unique not null ,
    price          decimal,
    status         text check ( status in ('CREATED', 'PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED') ),
    transaction_id text             NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL
);