package com.bmcho.hightrafficboard.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public Queue queue() {
        return new Queue("board-notification", true);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue("send_notification.email", true);
    }
    @Bean
    public Queue smsQueue() {
        return new Queue("send_notification.sms", true);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("send_notification_exchange");
    }

    @Bean
    public Binding bindingSmsQueue(Queue smsQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(smsQueue).to(fanoutExchange);
    }

    @Bean
    public Binding bindingEmailQueue(Queue emailQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(emailQueue).to(fanoutExchange);
    }
}
