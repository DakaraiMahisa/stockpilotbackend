package com.stockpilot.backend.catalog.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductRabbitConfig {

    public static final String PRODUCT_EXCHANGE = "product.exchange";

    public static final String PRODUCT_CREATED_QUEUE = "product.created.queue";
    public static final String PRODUCT_UPDATED_QUEUE = "product.updated.queue";

    public static final String PRODUCT_CREATED_ROUTING_KEY = "product.created";
    public static final String PRODUCT_UPDATED_ROUTING_KEY = "product.updated";

    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(PRODUCT_EXCHANGE, true, false);
    }

    @Bean
    public Queue productCreatedQueue() {
        return QueueBuilder
                .durable(PRODUCT_CREATED_QUEUE)
                .build();
    }

    @Bean
    public Queue productUpdatedQueue() {
        return QueueBuilder
                .durable(PRODUCT_UPDATED_QUEUE)
                .build();
    }

    @Bean
    public Binding productCreatedBinding() {
        return BindingBuilder
                .bind(productCreatedQueue())
                .to(productExchange())
                .with(PRODUCT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding productUpdatedBinding() {
        return BindingBuilder
                .bind(productUpdatedQueue())
                .to(productExchange())
                .with(PRODUCT_UPDATED_ROUTING_KEY);
    }
}