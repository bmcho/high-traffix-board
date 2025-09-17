package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.controller.notice.dto.WriteNoticeRequest;
import com.bmcho.hightrafficboard.entity.NoticeEntity;
import com.bmcho.hightrafficboard.entity.UserEntity;
import com.bmcho.hightrafficboard.entity.mongo.UserNotificationHistory;
import com.bmcho.hightrafficboard.exception.NoticeException;
import com.bmcho.hightrafficboard.repository.NoticeRepository;
import com.bmcho.hightrafficboard.repository.mongo.UserNotificationHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserNotificationHistoryRepository userNotificationHistoryRepository;

    private final UserService userService;

    @Transactional
    public NoticeEntity writeNotice(WriteNoticeRequest dto) {
        UserEntity userEntity = userService.getCurrentUser();

        NoticeEntity notice = new NoticeEntity(
            dto.getTitle(),
            dto.getContent(),
            userEntity
        );
        noticeRepository.save(notice);
        return notice;
    }

    public NoticeEntity getNotice(Long noticeId) {
        UserEntity userEntity = userService.getCurrentUser();
        NoticeEntity noticeEntity = noticeRepository.findById(noticeId)
            .orElseThrow(NoticeException.NoticeDoesNotExistException::new);

        UserNotificationHistory userNotificationHistory = UserNotificationHistory.builder()
            .title("공지사항이 작성되었습니다.")
            .content(noticeEntity.getTitle())
            .userId(userEntity.getId())
            .noticeId(noticeId)
            .isRead(true)
            .createdDate(noticeEntity.getCreatedAt())
            .updatedDate(LocalDateTime.now())
            .build();
        userNotificationHistoryRepository.save(userNotificationHistory);
        return noticeEntity;
    }
}