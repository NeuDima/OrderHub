package com.neupokoev.paymentservice.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.retry.max-attempts:3}")
    private int retryMaxAttempts;

    private Map<String, Object> baseProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, retryMaxAttempts);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000);

//        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "payment-service-tx-producer");
        return props;
    }

    @Bean
    public ProducerFactory<String, Object> transactionalProducerFactory() {
        DefaultKafkaProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(baseProducerConfigs());
        factory.setTransactionIdPrefix("payment-tx-");
        return factory;
    }

    @Bean
    public ProducerFactory<String, Object> reliableProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseProducerConfigs());
    }

    @Bean
    public KafkaTemplate<String, Object> transactionalKafkaTemplate() {
        return new KafkaTemplate<>(transactionalProducerFactory());
    }

    @Bean
    @Primary
    public KafkaTemplate<String, Object> reliableKafkaTemplate() {
        return new KafkaTemplate<>(reliableProducerFactory());
    }

//    @Bean
//    public KafkaTemplate<String, Object> retryKafkaTemplate(ProducerFactory<String, Object> producerFactory) {
//        return new KafkaTemplate<>(producerFactory);
//    }
}
