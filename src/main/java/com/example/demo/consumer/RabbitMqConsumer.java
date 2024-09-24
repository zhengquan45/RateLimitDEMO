package com.example.demo.consumer;

import com.example.demo.anno.RateLimited;
import com.example.demo.dto.MessageBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Component
@AllArgsConstructor
public class RabbitMqConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "yourQueueName")
    public void receiveMessage(Message message) throws IOException {
        System.out.println("Received message: " + message.getBody());
        invoke(message);
    }


    public String blockHandleMethod(Message message) {
        rabbitTemplate.send("yourExchangeName", "yourRoutingKey", message);
        return "被限流了";
    }

    @RateLimited(limitForPeriod = 3, limitRefreshPeriod = 20, blockHandle = "blockHandleMethod")
    public void invoke(Message message) throws IOException {
        MessageBody messageBody = objectMapper.readValue(message.getBody(), MessageBody.class);
        // 使用反射调用方法
        try {
            Class<?> clazz = Class.forName(messageBody.getClassName()); // 替换为你的服务类
            Method method = clazz.getMethod(messageBody.getMethod(), getParameterTypes(messageBody.getParams()));
            Object result = method.invoke(clazz.newInstance(), messageBody.getParams());
            System.out.println("Method result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class<?>[] getParameterTypes(Object[] params) {
        return Arrays.stream(params)
                .map(param -> param != null ? param.getClass() : Object.class)
                .toArray(Class[]::new);
    }
}