package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.entity.CommentEntity;
import com.bmcho.hightrafficboard.entity.NoticeEntity;
import com.bmcho.hightrafficboard.entity.UserEntity;
import com.bmcho.hightrafficboard.entity.mongo.UserNotificationHistory;
import com.bmcho.hightrafficboard.repository.NoticeRepository;
import com.bmcho.hightrafficboard.repository.mongo.UserNotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserNotificationHistoryService {

    private final NoticeRepository noticeRepository;
    private final UserNotificationHistoryRepository userNotificationHistoryRepository;

    private final UserService userService;


    public void insertArticleNotification(ArticleEntity entity, Long userId) {
        UserNotificationHistory history = UserNotificationHistory.builder()
            .title("글이 작성되었습니다.")
            .title(entity.getTitle())
            .userId(userId)
            .build();
        userNotificationHistoryRepository.save(history);
    }

    public void insertCommentNotification(CommentEntity entity, Long userId) {
        UserNotificationHistory history = UserNotificationHistory.builder()
            .title("댓글이 작성되었습니다.")
            .title(entity.getContent())
            .userId(userId)
            .build();
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

    public List<UserNotificationHistory> getNotificationList() {
        UserEntity userEntity = userService.getCurrentUser();

        LocalDateTime weekDate = LocalDateTime.now().minusWeeks(7);
        // 일주일 전 알림만 노출
        List<UserNotificationHistory> userHistories = userNotificationHistoryRepository
            .findByUserIdAndCreatedDateAfter(userEntity.getId(), weekDate);

        // 일주일 전 공지만 추출
        List<NoticeEntity> notices = noticeRepository.findByCreatedDate(weekDate);

        // 공지 알림만 map으로 변환 (중복 제거용)
        Map<Long, UserNotificationHistory> noticeHistoryMap = userHistories.stream()
            .filter(history -> history.getNoticeId() != null)
            .collect(Collectors.toMap(
                UserNotificationHistory::getNoticeId,
                history -> history,
                (existing, replacement) -> existing // 중복 발생 시 기존 값 유지
            ));

        // 일반 알림 (공지사항 제외)
        List<UserNotificationHistory> results = userHistories.stream()
            .filter(history -> history.getNoticeId() == null)
            .collect(Collectors.toCollection(ArrayList::new));

        // 공지 알림 병합
        for (NoticeEntity notice : notices) {
            UserNotificationHistory history = noticeHistoryMap.getOrDefault(
                notice.getId(),
                UserNotificationHistory.builder()
                    .title("공지사항이 작성되었습니다.")
                    .content(notice.getTitle())
                    .userId(userEntity.getId())
                    .isRead(false)
                    .noticeId(notice.getId())
                    .createdDate(notice.getCreatedAt())
                    .updatedDate(null)
                    .build()
            );
            results.add(history);
        }
        return results;
    }
}