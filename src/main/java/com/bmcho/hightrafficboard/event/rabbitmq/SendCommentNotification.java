package com.bmcho.hightrafficboard.event.rabbitmq;

public record SendCommentNotification(Long commentId, Long userId) implements EventMessage {
    @Override public String type() { return EventType.SEND_COMMENT_NOTIFICATION; }
}