package com.bmcho.hightrafficboard.event.rabbitmq;

public record WriteComment(Long commentId) implements EventMessage {
    @Override public String type() { return EventType.WRITE_COMMENT; }
}
