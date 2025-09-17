package com.bmcho.hightrafficboard.controller.notice.dto;

import com.bmcho.hightrafficboard.entity.NoticeEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private String author;


    public static NoticeResponse from(NoticeEntity entity) {
        return NoticeResponse.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .content(entity.getContent())
            .author(entity.getAuthor().getUsername())
            .build();
    }

}
