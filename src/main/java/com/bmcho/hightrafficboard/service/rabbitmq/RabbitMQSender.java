package com.bmcho.hightrafficboard.service.rabbitmq;

import com.bmcho.hightrafficboard.event.rabbitmq.EventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQSender {
    private final RabbitTemplate rabbitTemplate;

    public void send(EventMessage eventMessage) {
        rabbitTemplate.convertAndSend("", "board-notification", eventMessage);
    }
}