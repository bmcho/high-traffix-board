package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.entity.CommentEntity;
import com.bmcho.hightrafficboard.entity.mongo.UserNotificationHistory;
import com.bmcho.hightrafficboard.repository.mongo.UserNotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserNotificationHistoryService {

    private final UserNotificationHistoryRepository userNotificationHistoryRepository;


    public void insertArticleNotification(ArticleEntity entity, Long userId) {
        UserNotificationHistory history = new UserNotificationHistory(
            "글이 작성되었습니다.",
            entity.getTitle(),
            userId
        );
        userNotificationHistoryRepository.save(history);
    }

    public void insertCommentNotification(CommentEntity entity, Long userId) {
        UserNotificationHistory history = new UserNotificationHistory(
            "댓글이 작성되었습니다.",
            entity.getContent(),
            userId
        );
        userNotificationHistoryRepository.save(history);
    }

    public void readNotification(String id) {
        Optional<UserNotificationHistory> history = userNotificationHistoryRepository.findById(id);
        if (history.isEmpty()) {
            return;
        }
        history.get().setIsRead(true);
        history.get().setUpdatedDate(LocalDateTime.now());
        userNotificationHistoryRepository.save(history.get());
    }
}