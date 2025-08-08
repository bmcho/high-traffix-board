package com.bmcho.hightrafficboard.controller.Article.dto;

import lombok.Getter;

@Getter
public class WriteArticleRequest {
    long boardId;
    String title;
    String content;
}
