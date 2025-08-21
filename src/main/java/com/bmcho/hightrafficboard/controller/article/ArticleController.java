package com.bmcho.hightrafficboard.controller.article;

import com.bmcho.hightrafficboard.controller.article.dto.ArticleResponse;
import com.bmcho.hightrafficboard.controller.article.dto.ArticleWithCommentsResponse;
import com.bmcho.hightrafficboard.controller.article.dto.UpdateArticleRequest;
import com.bmcho.hightrafficboard.controller.article.dto.WriteArticleRequest;
import com.bmcho.hightrafficboard.controller.BoardApiResponse;
import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.service.ArticleService;
import com.bmcho.hightrafficboard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/{boardId}/articles")
    public BoardApiResponse<ArticleResponse> writeArticle(@PathVariable("boardId") Long boardId, @RequestBody WriteArticleRequest writeArticleDto) {

        ArticleEntity articleEntity = articleService.writeArticle(boardId, writeArticleDto);
        ArticleResponse response = ArticleResponse.from(articleEntity);
        return BoardApiResponse.ok(response);
    }

    @GetMapping("/{boardId}/articles")
    public BoardApiResponse<List<ArticleResponse>> getArticle(@PathVariable Long boardId,
                                                              @RequestParam(required = false) Long lastId,
                                                              @RequestParam(required = false) Long firstId) {

        List<ArticleEntity> articleEntityList;

        if (lastId != null)
            articleEntityList = articleService.getOldArticle(boardId, lastId);
        else if (firstId != null)
            articleEntityList = articleService.getNewArticle(boardId, firstId);
        else
            articleEntityList = articleService.firstGetArticle(boardId);

        List<ArticleResponse> response = articleEntityList.stream().map(ArticleResponse::from).toList();
        return BoardApiResponse.ok(response);
    }

    @GetMapping("/{boardId}/articles/{articleId}")
    public BoardApiResponse<ArticleWithCommentsResponse> getArticleWithComment(@PathVariable Long boardId, @PathVariable Long articleId) {
        CompletableFuture<ArticleEntity> articleEntityFuture = articleService.getArticleWithComment(boardId, articleId);
        ArticleEntity articleEntity = articleEntityFuture.getNow(null);
        if (articleEntity == null)
            return BoardApiResponse.ok(null);
        else
            return BoardApiResponse.ok(ArticleWithCommentsResponse.from(articleEntity));
    }

    @PutMapping("/{boardId}/articles/{articleId}")
    public BoardApiResponse<ArticleResponse> updateArticle(@PathVariable Long boardId, @PathVariable Long articleId, @RequestBody UpdateArticleRequest dto) {
        ArticleEntity articleEntity = articleService.updateArticle(boardId, articleId, dto);
        ArticleResponse response = ArticleResponse.from(articleEntity);
        return BoardApiResponse.ok(response);
    }

    @DeleteMapping("/{boardId}/articles/{articleId}")
    public BoardApiResponse<String> deleteArticle(@PathVariable Long boardId, @PathVariable Long articleId) {
        articleService.deleteArticle(boardId, articleId);
        return BoardApiResponse.ok("article is deleted");
    }

}
