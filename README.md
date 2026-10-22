# OrderHub

### Проект для обучения работы с микросервесной архитектурой. 
### Стэк: 
* Java, Spring Boot, Apache Kafka;
* PostgreSQL, Hibernate, Redis.

### Проект включает 4 микросервиса:
* order-servise - позволяет создать по REST API заказ с сохранением в БД, а также отправляет данные о заказе в payment-service для его оплаты;
* payment-service - производит фейк-оплату (т.к. не подключена система оплаты) заказа и возвращает результат работы;
* saga-orchestration - микросервис, реализующий паттер saga для коммуникации order-servise и payment-service через kafka;
* event-contraction - содержит events и commands, которые передаются по средствам kafka.

### В проекте попытался достичь семантику exactly-once засчёт:
* Идемпотентности продюсера;
* Паттерна Outbox на стороне producer;
* Паттерна Inbox и распределённой блокировки через Redis.
