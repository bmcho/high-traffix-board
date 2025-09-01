package com.bmcho.hightrafficboard.controller.article.dto;


import com.bmcho.hightrafficboard.controller.comment.dto.CommentResponse;
import com.bmcho.hightrafficboard.controller.user.dto.UserResponse;
import com.bmcho.hightrafficboard.entity.ArticleEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ArticleWithCommentsResponse {

    private final long id;
    private final String title;
    private final String contents;
    private final long viewCount;

    private final UserResponse author;
    private final List<CommentResponse> comments;

    public static ArticleWithCommentsResponse from(ArticleEntity entity) {
        return ArticleWithCommentsResponse.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .contents(entity.getContent())
            .viewCount(entity.getViewCount())
            .author(UserResponse.from(entity.getAuthor()))
            .comments(entity.getComments()
                .stream().map(CommentResponse::from).toList()
            )
            .build();
    }
}
