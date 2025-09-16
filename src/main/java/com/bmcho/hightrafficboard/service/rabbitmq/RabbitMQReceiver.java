
package com.bmcho.hightrafficboard.service.rabbitmq;

import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.entity.CommentEntity;
import com.bmcho.hightrafficboard.event.rabbitmq.EventMessage;
import com.bmcho.hightrafficboard.event.rabbitmq.SendCommentNotification;
import com.bmcho.hightrafficboard.event.rabbitmq.WriteArticle;
import com.bmcho.hightrafficboard.event.rabbitmq.WriteComment;
import com.bmcho.hightrafficboard.repository.ArticleRepository;
import com.bmcho.hightrafficboard.repository.CommentRepository;
import com.bmcho.hightrafficboard.service.UserNotificationHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RabbitMQReceiver {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserNotificationHistoryService userNotificationHistoryService;

    private final RabbitMQSender rabbitMQSender;

    @RabbitListener(queues = "send_notification.email")
    public void emailReceive(String message) {
        System.out.println("Received Message(email): " + message);
    }

    @RabbitListener(queues = "send_notification.sms")
    public void smsReceive(String message) {
        System.out.println("Received Message(sms): " + message);
    }

    @RabbitListener(queues = "board-notification")
    public void receive(EventMessage eventMessage) {

        if (eventMessage instanceof WriteComment comment) {
            this.sendCommentNotification(comment);
            return;
        }

        if (eventMessage instanceof WriteArticle article) {
            this.sendArticleNotification(article);
            return;
        }
    }

    private void sendCommentNotification(WriteComment eventMessage) {
        // 알림 전송
        CommentEntity comment = commentRepository.findById(eventMessage.commentId())
            .orElse(null);
        if (comment == null) {
            return;
        }

        ArticleEntity article = comment.getArticle();

        HashSet<Long> userSet = new HashSet<>();
        // 댓글 작성한 본인
        userSet.add(eventMessage.userId());
        // 글 작성자
        userSet.add(article.getAuthor().getId());
        // 댓글 작성자 모두
        List<CommentEntity> comments = commentRepository.findByArticleId(article.getId());
        for (CommentEntity articleComment : comments) {
            userSet.add(articleComment.getAuthor().getId());
        }
        for (Long userId : userSet) {
            rabbitMQSender.send(new SendCommentNotification(eventMessage.commentId(), userId));
        }
    }

    private void sendArticleNotification(WriteArticle eventMessage) {
        articleRepository.findById(eventMessage.articleId())
            .ifPresent(entity -> userNotificationHistoryService.insertArticleNotification(entity, entity.getId())
            );
    }
}