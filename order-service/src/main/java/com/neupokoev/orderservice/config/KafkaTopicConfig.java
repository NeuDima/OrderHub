package com.neupokoev.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.topic.order-create-event-topic}")
    private String orderCreateEventTopic;

    @Value("${app.topic.order-command-topic}")
    private String orderCommandTopic;

    @Bean
    public NewTopic orderCreateTopic() {
        return TopicBuilder
                .name(orderCreateEventTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCommandTopic() {
        return TopicBuilder
                .name(orderCommandTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
