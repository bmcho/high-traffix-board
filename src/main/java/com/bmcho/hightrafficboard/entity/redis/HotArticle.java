package com.bmcho.hightrafficboard.entity.redis;

import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.entity.UserEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HotArticle {
    private Long id;
    private String title;
    private String content;
    private UserEntity author;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long viewCount;

    public static HotArticle fromEntity(ArticleEntity entity) {
        return HotArticle.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .content(entity.getContent())
            .author(entity.getAuthor())
            .createdAt(entity.getCreatedAt())
            .modifiedAt(entity.getModifiedAt())
            .viewCount(entity.getViewCount())
            .build();
    }
}