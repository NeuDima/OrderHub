package com.neupokoev.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.topic.payment-event}")
    private String paymentEventTopic;

    @Value("${app.topic.payment-command}")
    private String paymentCommandTopic;

    @Bean
    public NewTopic paymentEventTopic() {
        return TopicBuilder
                .name(paymentEventTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentCommandTopic() {
        return TopicBuilder
                .name(paymentCommandTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
