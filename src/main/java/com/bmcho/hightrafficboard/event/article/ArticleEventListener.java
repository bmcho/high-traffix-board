package com.bmcho.hightrafficboard.event.article;

import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.exception.ArticleException;
import com.bmcho.hightrafficboard.repository.ArticleRepository;
import com.bmcho.hightrafficboard.service.BoardService;
import com.bmcho.hightrafficboard.service.ElasticSearchService;
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

    @Async
    @Transactional
    @EventListener
    public void handleArticleViewed(ArticleViewedEvent event) {
        log.debug("Handling ArticleViewedEvent for articleId={}", event.articleId());

        boardService.getBoard(event.boardId());
        ArticleEntity article = articleRepository.findById(event.articleId())
            .filter(a -> !a.getIsDeleted())
            .orElseThrow(ArticleException.ArticleDoesNotExistException::new);
        long viewCount = article.getViewCount() + 1;
        article.setViewCount(viewCount);
        elasticSearchService.incrementViewCount(article.getId().toString(), viewCount);
        articleRepository.save(article);
    }
}