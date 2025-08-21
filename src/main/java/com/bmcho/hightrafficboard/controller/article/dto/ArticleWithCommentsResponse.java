package com.bmcho.hightrafficboard.controller.article.dto;


import com.bmcho.hightrafficboard.controller.user.dto.UserResponse;
import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.entity.CommentEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ArticleWithCommentsResponse {

    private final long id;
    private final String title;
    private final String contents;

    private final UserResponse author;
    private final List<CommentEntity> comments;
//    private final BoardResponse board;

    public static ArticleWithCommentsResponse from(ArticleEntity entity) {
        return ArticleWithCommentsResponse.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .contents(entity.getContent())
            .author(UserResponse.from(entity.getAuthor()))
            .comments(entity.getComments())
//            .board(BoardResponse.from(entity.getBoard()))
            .build();
    }
}
