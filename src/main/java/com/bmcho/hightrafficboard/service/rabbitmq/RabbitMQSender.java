package com.bmcho.hightrafficboard.service.rabbitmq;

import com.bmcho.hightrafficboard.event.rabbitmq.EventMessage;
import com.bmcho.hightrafficboard.event.rabbitmq.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RabbitMQSender {
    private final RabbitTemplate rabbitTemplate;

    public void send(EventMessage eventMessage) {
        if (Objects.equals(eventMessage.type(), EventType.SEND_COMMENT_NOTIFICATION)) {
            rabbitTemplate.convertAndSend("send_notification_exchange", "", eventMessage);
        } else {
            rabbitTemplate.convertAndSend("", "board-notification", eventMessage);
        }
    }
}