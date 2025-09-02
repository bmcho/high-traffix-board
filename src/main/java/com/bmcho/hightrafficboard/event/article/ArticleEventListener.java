package com.bmcho.hightrafficboard.event.article;

import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.exception.ArticleException;
import com.bmcho.hightrafficboard.repository.ArticleRepository;
import com.bmcho.hightrafficboard.service.BoardService;
import com.bmcho.hightrafficboard.service.ElasticSearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleEventListener {

    private final ArticleRepository articleRepository;
    private final BoardService boardService;
    private final ElasticSearchService elasticSearchService;

    private final ObjectMapper objectMapper;

    @Async
    @Transactional
    @EventListener
    public void handleArticleViewed(ArticleViewedEvent event) {
        log.debug("Handling ArticleViewedEvent for articleId={}", event.articleId());

        boardService.getBoard(event.boardId());
        ArticleEntity article = articleRepository.findById(event.articleId())
            .filter(a -> !a.getIsDeleted())
            .orElseThrow(ArticleException.ArticleDoesNotExistException::new);

        article.setViewCount(article.getViewCount() + 1);
        elasticSearchService.incrementViewCount(article.getId().toString(), article.getViewCount());
        articleRepository.save(article);
    }
}