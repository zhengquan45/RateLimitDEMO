package com.example.demo.demos.web;


import com.example.demo.dto.MessageBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class RabbitMQController {

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/sendMessage")
    @ResponseBody
    public void sendMessage(@RequestBody MessageBody messageBody) throws JsonProcessingException {
        MessageProperties props = new MessageProperties();
        props.setPriority(10);
        String json = objectMapper.writeValueAsString(messageBody);
        Message msg = new Message(json.getBytes(), props);
        rabbitTemplate.send("yourExchangeName", "yourRoutingKey", msg);
    }

}
