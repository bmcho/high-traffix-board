package com.bmcho.hightrafficboard.controller.article.dto;


import com.bmcho.hightrafficboard.controller.user.dto.UserResponse;
import com.bmcho.hightrafficboard.entity.ArticleEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArticleResponse {

    private final long id;
    private final String title;
    private final String contents;

    private final UserResponse author;
//    private final BoardResponse board;

    public static ArticleResponse from(ArticleEntity entity) {
        return ArticleResponse.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .contents(entity.getContent())
            .author(UserResponse.from(entity.getAuthor()))
//            .board(BoardResponse.from(entity.getBoard()))
            .build();
    }
}
