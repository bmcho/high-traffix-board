package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.controller.article.dto.UpdateArticleRequest;
import com.bmcho.hightrafficboard.controller.article.dto.WriteArticleRequest;
import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.entity.BoardEntity;
import com.bmcho.hightrafficboard.entity.CommentEntity;
import com.bmcho.hightrafficboard.entity.UserEntity;
import com.bmcho.hightrafficboard.entity.audit.BaseEntity;
import com.bmcho.hightrafficboard.entity.audit.MutableBaseEntity;
import com.bmcho.hightrafficboard.entity.redis.HotArticle;
import com.bmcho.hightrafficboard.event.rabbitmq.WriteArticle;
import com.bmcho.hightrafficboard.event.spring.ArticleViewedEvent;
import com.bmcho.hightrafficboard.exception.ArticleException;
import com.bmcho.hightrafficboard.repository.ArticleRepository;
import com.bmcho.hightrafficboard.repository.CommentRepository;
import com.bmcho.hightrafficboard.service.rabbitmq.RabbitMQSender;
import com.bmcho.hightrafficboard.task.DailyHotArticleTasks;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    private final BoardService boardService;
    private final UserService userService;
    private final ElasticSearchService elasticSearchService;

    private final ApplicationEventPublisher eventPublisher;
    private final RabbitMQSender rabbitMQSender;
    private final RedisTemplate<String, Object> redisTemplate;

    public ArticleEntity getArticle(Long articleId) {
        return articleRepository.findById(articleId)
            .filter(a -> !a.getIsDeleted())
            .orElseThrow(ArticleException.ArticleDoesNotExistException::new);
    }

    private HotArticle findHotArticle(Long articleId) {
        String yesterdayKey = DailyHotArticleTasks.YESTERDAY_REDIS_KEY + articleId;
        String weekKey = DailyHotArticleTasks.WEEK_REDIS_KEY + articleId;

        return Stream.of(yesterdayKey, weekKey)
            .map(key -> redisTemplate.opsForHash().get(key, articleId))
            .filter(Objects::nonNull)
            .map(HotArticle.class::cast)
            .findFirst()
            .orElse(null);
    }

    @Async
    protected CompletableFuture<ArticleEntity> getArticleAsync(Long boardId, Long articleId) {
        BoardEntity boardEntity = boardService.getBoard(boardId);
        HotArticle hotArticle = findHotArticle(articleId);
        if (hotArticle != null) {
            return CompletableFuture.completedFuture(ArticleEntity.fromHotArticle(hotArticle));
        }

        ArticleEntity article = getArticle(articleId);
        return CompletableFuture.completedFuture(article);
    }

    @Async
    protected CompletableFuture<List<CommentEntity>> getCommentsAsync(Long articleId) {
        return CompletableFuture.completedFuture(commentRepository.findByArticleId(articleId));
    }

    public CompletableFuture<ArticleEntity> getArticleWithComment(Long boardId, Long articleId) {
        CompletableFuture<ArticleEntity> articleFuture = this.getArticleAsync(boardId, articleId);

        //조회수 증가 이벤트 발행 (비동기)
        eventPublisher.publishEvent(new ArticleViewedEvent(boardId, articleId));
        CompletableFuture<List<CommentEntity>> commentsFuture = this.getCommentsAsync(articleId);

        return CompletableFuture.allOf(articleFuture, commentsFuture)
            .handle((res, ex) -> {
                if (ex != null) {
                    log.error("[ArticleService.getArticleWithComment] error: {}", ex.getMessage(), ex);
                    return null;
                }
                ArticleEntity article = articleFuture.join();
                List<CommentEntity> comments = commentsFuture.join();
                article.setComments(comments);
                return article;
            });
    }

    public List<ArticleEntity> firstGetArticles(Long boardId) {
        return articleRepository.findTop10ByBoardIdOrderByCreatedAtDesc(boardId);
    }

    public List<ArticleEntity> getOldArticle(Long boardId, Long articleId) {
        return articleRepository.findTop10ByBoardIdAndArticleIdLessThanOrderByCreatedAtDesc(boardId, articleId);
    }

    public List<ArticleEntity> getNewArticle(Long boardId, Long articleId) {
        return articleRepository.findTop10ByBoardIdAndArticleIdGreaterThanOrderByCreatedAtDesc(boardId, articleId);
    }

    @Transactional
    public ArticleEntity writeArticle(Long boardId, WriteArticleRequest dto) {
        UserEntity author = userService.getCurrentUser();
        BoardEntity board = boardService.getBoard(boardId);

        if (!isArticleWritable(author.getUsername())) {
            throw new ArticleException.ArticleNotEditedByRateLimitException();
        }

        ArticleEntity article = new ArticleEntity(
            author,
            board,
            dto.getTitle(),
            dto.getContent()
        );
        articleRepository.save(article);
        elasticSearchService.indexArticleDocument(article);
        WriteArticle writeArticle = new WriteArticle(article.getId(), author.getId());
        rabbitMQSender.send(writeArticle);
        return article;
    }

    @Transactional
    public ArticleEntity updateArticle(Long boardId, Long articleId, UpdateArticleRequest dto) {
        UserEntity author = userService.getCurrentUser();
        boardService.getBoard(boardId);
        ArticleEntity article = getArticle(articleId);

        if (!isArticleWritable(author.getUsername())) {
            throw new ArticleException.ArticleNotEditedByRateLimitException();
        }

        if (article.getAuthor() != author) {
            throw new ArticleException.ArticleAuthorDifferentException();
        }

        if (dto.getTitle() != null) {
            article.setTitle(dto.getTitle());
        }

        if (dto.getContent() != null) {
            article.setContent(dto.getContent());
        }

        articleRepository.save(article);
        elasticSearchService.indexArticleDocument(article);
        return article;
    }

    @Transactional
    public boolean deleteArticle(Long boardId, Long articleId) {
        UserEntity author = userService.getCurrentUser();
        boardService.getBoard(boardId);
        ArticleEntity article = getArticle(articleId);

        if (!isArticleWritable(author.getUsername())) {
            throw new ArticleException.ArticleNotEditedByRateLimitException();
        }

        if (article.getAuthor() != author) {
            throw new ArticleException.ArticleAuthorDifferentException();
        }

        article.setIsDeleted(true);
        articleRepository.save(article);
        elasticSearchService.indexArticleDocument(article);
        return true;
    }

    private boolean isArticleWritable(String username) {
        LocalDateTime latest = Stream.of(
                articleRepository.findLatestArticleByAuthorUsernameOrderByCreatedAt(username)
                    .map(BaseEntity::getCreatedAt)
                    .orElse(null),
                articleRepository.findLatestArticleByAuthorUsernameOrderByModifiedAt(username)
                    .map(MutableBaseEntity::getModifiedAt)
                    .orElse(null)
            )
            .filter(Objects::nonNull)
            .max(LocalDateTime::compareTo)
            .orElse(null);

        return latest == null || isDifferenceMoreThanTowMinutes(latest);

    }

    private boolean isDifferenceMoreThanTowMinutes(LocalDateTime localDateTime) {
        LocalDateTime dateAsLocalDateTime = new Date().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        Duration duration = Duration.between(localDateTime, dateAsLocalDateTime);
        return Math.abs(duration.toSeconds()) > 10;
    }

    public List<ArticleEntity> searchArticles(String keyword) {
        Mono<List<Long>> articleIds = elasticSearchService.articleSearch(keyword);
        try {
            return articleRepository.findAllById(articleIds.toFuture().get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
