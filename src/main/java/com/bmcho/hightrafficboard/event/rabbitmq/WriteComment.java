package com.bmcho.hightrafficboard.event.rabbitmq;

public record WriteComment(Long commentId, Long userId) implements EventMessage {
    @Override public String type() { return EventType.WRITE_COMMENT; }
}
