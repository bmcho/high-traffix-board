package com.bmcho.hightrafficboard.service.rabbitmq;

import com.bmcho.hightrafficboard.event.rabbitmq.EventMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {
    private final RabbitTemplate rabbitTemplate;

    public RabbitMQSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(EventMessage eventMessage) {
        rabbitTemplate.convertAndSend("onion-notification", eventMessage.type());
    }
}