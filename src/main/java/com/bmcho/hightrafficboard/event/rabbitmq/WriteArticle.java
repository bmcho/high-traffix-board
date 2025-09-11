package com.bmcho.hightrafficboard.event.rabbitmq;

public record WriteArticle(Long articleId, Long userId) implements EventMessage {
    @Override public String type() { return EventType.WRITE_ARTICLE; }
}
