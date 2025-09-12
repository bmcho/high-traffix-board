
package com.bmcho.hightrafficboard.service.rabbitmq;

import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.entity.CommentEntity;
import com.bmcho.hightrafficboard.event.rabbitmq.EventMessage;
import com.bmcho.hightrafficboard.event.rabbitmq.SendCommentNotification;
import com.bmcho.hightrafficboard.event.rabbitmq.WriteComment;
import com.bmcho.hightrafficboard.repository.ArticleRepository;
import com.bmcho.hightrafficboard.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
@RequiredArgsConstructor
public class RabbitMQReceiver {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    private final RabbitMQSender rabbitMQSender;

    @RabbitListener(queues = "board-notification")
    public void receive(EventMessage eventMessage) {

        if (eventMessage instanceof WriteComment) {
            this.sendCommentNotification((WriteComment) eventMessage);
            return;
        }

        Timer timer = new Timer();
        // 10초 후에 실행될 작업을 Timer에 등록
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Received Message: " + eventMessage.type());
            }
        }, 5000); // 5초\
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
}