
package com.bmcho.hightrafficboard.event.rabbitmq;

public sealed interface EventMessage permits WriteComment, WriteArticle, SendCommentNotification {
    String type();
}
