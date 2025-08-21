package com.bmcho.hightrafficboard.controller.comment.dto;

import com.bmcho.hightrafficboard.controller.user.dto.UserResponse;
import com.bmcho.hightrafficboard.entity.CommentEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponse {

    private final long id;
    private final String contents;
    private final UserResponse author;

    public static CommentResponse from(CommentEntity entity) {
        return CommentResponse.builder()
            .id(entity.getId())
            .contents(entity.getContent())
            .author(UserResponse.from(entity.getAuthor()))
            .build();
    }
}
