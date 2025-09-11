package com.bmcho.hightrafficboard.event.rabbitmq;

public interface EventType {
    String WRITE_COMMENT = "write_comment";
    String WRITE_ARTICLE = "write_article";
    String SEND_COMMENT_NOTIFICATION = "send_comment_notification";
}