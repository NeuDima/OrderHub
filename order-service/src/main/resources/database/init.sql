create table orders
(
    id          serial primary key,
    description text      not null,
    create_at   timestamp not null,
    price       decimal   not null,
    status      text check ( status in ('CREATE', 'WAITING', 'PAID', 'FAILED', 'CANCEL'))
);