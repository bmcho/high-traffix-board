package com.bmcho.hightrafficboard.entity.mongo;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "userNotificationHistory")
@Getter
@Setter
public class UserNotificationHistory {

    @Id
    private String id;

    private String title;

    private String content;

    private Long userId;

    private Boolean isRead = false;

    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime updatedDate = LocalDateTime.now();

    public UserNotificationHistory(String title, String content, Long userId) {
        this.title = title;
        this.content =content;
        this.userId = userId;
    }
}