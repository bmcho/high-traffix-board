package com.bmcho.hightrafficboard.controller.Article.dto;

import com.bmcho.hightrafficboard.entity.BoardEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardResponse {

    private final long id;
    private final String title;
    private final String description;

    public static BoardResponse from(BoardEntity entity) {
        return BoardResponse.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .build();
    }
}
