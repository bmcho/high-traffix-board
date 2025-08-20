package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.config.security.BoardUser;
import com.bmcho.hightrafficboard.controller.Article.dto.UpdateArticleRequest;
import com.bmcho.hightrafficboard.controller.Article.dto.WriteArticleRequest;
import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.entity.BoardEntity;
import com.bmcho.hightrafficboard.entity.UserEntity;
import com.bmcho.hightrafficboard.exception.ArticleException;
import com.bmcho.hightrafficboard.exception.BoardException;
import com.bmcho.hightrafficboard.exception.UserException;
import com.bmcho.hightrafficboard.repository.ArticleRepository;
import com.bmcho.hightrafficboard.repository.BoardRepository;
import com.bmcho.hightrafficboard.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public List<ArticleEntity> firstGetArticle(Long boardId) {
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
        // filter 에서 검증된 유저, 예외처리 x
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        BoardUser boardUser = (BoardUser) authentication.getPrincipal();

        UserEntity author = userRepository.findById(boardUser.getId())
            .orElseThrow(UserException.UserDoesNotExistException::new);

        BoardEntity board = boardRepository.findById(boardId)
            .orElseThrow(BoardException.BoardDoesNotExistException::new);

        ArticleEntity article = new ArticleEntity(
            author,
            board,
            dto.getTitle(),
            dto.getContent()
        );

        return articleRepository.save(article);
    }

    @Transactional
    public ArticleEntity updateArticle(Long boardId, Long articleId, UpdateArticleRequest dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        BoardUser boardUser = (BoardUser) authentication.getPrincipal();
        ArticleEntity article = validateArticleAccess(boardId, articleId, boardUser.getUsername());

        if (dto.getTitle() != null) {
            article.setTitle(dto.getTitle());
        }

        if (dto.getContent() != null) {
            article.setContent(dto.getContent());
        }

        articleRepository.save(article);
        return article;
    }

    @Transactional
    public boolean deleteArticle(Long boardId, Long articleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        BoardUser boardUser = (BoardUser) authentication.getPrincipal();
        ArticleEntity article = validateArticleAccess(boardId, articleId, boardUser.getUsername());

        article.setIsDeleted(true);
        articleRepository.save(article);
        return true;
    }

    private ArticleEntity validateArticleAccess(Long boardId, Long articleId, String username) {

        UserEntity author = userRepository.findByUsername(username)
            .orElseThrow(UserException.UserDoesNotExistException::new);

        BoardEntity board = boardRepository.findById(boardId)
            .orElseThrow(BoardException.BoardDoesNotExistException::new);

        ArticleEntity article = articleRepository.findById(articleId)
            .orElseThrow(ArticleException.ArticleDoesNotExistException::new);

        if (!article.getAuthor().equals(author)) {
            throw new ArticleException.ArticleAuthorDifferentException();
        }

        if (!this.isCanUpdateArticle(username)) {
            throw new ArticleException.ArticleNotEditedByRateLimitException();
        }

        return article;
    }

    private boolean isCanWriteArticle(String username) {
        ArticleEntity latestArticle = articleRepository.findLatestArticleByAuthorUsernameOrderByCreatedAt(username);
        return this.isDifferenceMoreThanFiveMinutes(latestArticle.getCreatedAt());
    }

    private boolean isCanUpdateArticle(String username) {
        ArticleEntity latestArticle = articleRepository.findLatestArticleByAuthorUsernameOrderByModifiedAt(username);
        return this.isDifferenceMoreThanFiveMinutes(latestArticle.getModifiedAt());
    }

    private boolean isDifferenceMoreThanFiveMinutes(LocalDateTime localDateTime) {
        LocalDateTime dateAsLocalDateTime = new Date().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        Duration duration = Duration.between(localDateTime, dateAsLocalDateTime);

        return Math.abs(duration.toMinutes()) > 5;
    }

}
