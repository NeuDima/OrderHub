package com.neupokoev.sagaorchestration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class SagaOrchestrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SagaOrchestrationApplication.class, args);
    }

}
