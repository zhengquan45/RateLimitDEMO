package com.example.demo.config;

import io.vavr.collection.Map;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashMap;

@Configuration
//@EnableRabbit
public class RabbitMqConfig {

    @Bean
    public Queue yourQueue() {
        HashMap<String, Object> configMap = new HashMap<>();
        configMap.put("x-max-priority",10);
        return new Queue("yourQueueName", true,false,false,configMap);  // 队列名称和是否持久化
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("yourExchangeName");  // 交换机名称
    }

    @Bean
    public Binding binding(Queue yourQueue, TopicExchange exchange) {
        return BindingBuilder.bind(yourQueue).to(exchange).with("yourRoutingKey");  // 路由键
    }
}