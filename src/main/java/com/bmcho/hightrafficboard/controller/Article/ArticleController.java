package com.bmcho.hightrafficboard.controller.Article;

import com.bmcho.hightrafficboard.controller.Article.dto.ArticleResponse;
import com.bmcho.hightrafficboard.controller.Article.dto.WriteArticleRequest;
import com.bmcho.hightrafficboard.controller.BoardApiResponse;
import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/{boardId}/articles")
    public BoardApiResponse<ArticleResponse> writeArticle(@RequestBody WriteArticleRequest writeArticleDto) {

        ArticleEntity articleEntity = articleService.writeArticle(writeArticleDto);
        ArticleResponse response = ArticleResponse.from(articleEntity);
        return BoardApiResponse.ok(response);
    }

}
