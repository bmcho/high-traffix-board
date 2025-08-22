package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.controller.comment.dto.WriteCommentRequest;
import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.entity.CommentEntity;
import com.bmcho.hightrafficboard.entity.UserEntity;
import com.bmcho.hightrafficboard.entity.audit.BaseEntity;
import com.bmcho.hightrafficboard.entity.audit.MutableBaseEntity;
import com.bmcho.hightrafficboard.exception.ArticleException;
import com.bmcho.hightrafficboard.exception.CommentException;
import com.bmcho.hightrafficboard.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final ArticleService articleService;
    private final BoardService boardService;
    private final UserService userService;

    public CommentEntity getComment(Long commentId) {
        return commentRepository.findById(commentId)
            .filter(c -> !c.getIsDeleted())
            .orElseThrow(CommentException.CommentDoesNotExistException::new);
    }

    @Transactional
    public CommentEntity writeComment(Long boardId, Long articleId, WriteCommentRequest dto) {
        UserEntity author = userService.getCurrentUser();
        boardService.getBoard(boardId);
        ArticleEntity article = articleService.getArticle(articleId);

        if (!isCommentWritable(author.getUsername())) {
            throw new CommentException.CommentNotEditedByRateLimitException();
        }

        CommentEntity comment = new CommentEntity(dto.getContent(), author, article);
        return commentRepository.save(comment);
    }

    @Transactional
    public CommentEntity updateComment(Long boardId, Long articleId, Long commentId, WriteCommentRequest dto) {
        UserEntity author = userService.getCurrentUser();
        boardService.getBoard(boardId);
        articleService.getArticle(articleId);
        CommentEntity comment = getComment(commentId);

        if (!isCommentWritable(author.getUsername())) {
            throw new CommentException.CommentNotEditedByRateLimitException();
        }

        if (!comment.getAuthor().equals(author)) {
            throw new CommentException.CommentAuthorDifferentException();
        }

        if (dto.getContent() != null) {
            comment.setContent(dto.getContent());
        }

        return commentRepository.save(comment);
    }

    @Transactional
    public boolean deleteComment(Long boardId, Long articleId, Long commentId) {
        UserEntity author = userService.getCurrentUser();
        boardService.getBoard(boardId);
        articleService.getArticle(articleId);
        CommentEntity comment = getComment(commentId);

        if (!isCommentWritable(author.getUsername())) {
            throw new ArticleException.ArticleNotEditedByRateLimitException();
        }

        if (!comment.getAuthor().equals(author)) {
            throw new CommentException.CommentAuthorDifferentException();
        }

        comment.setIsDeleted(true);
        commentRepository.save(comment);
        return true;
    }

    private boolean isCommentWritable(String username) {
        LocalDateTime latest = Stream.of(
                commentRepository.findLatestCommentOrderByCreatedDate(username)
                    .map(BaseEntity::getCreatedAt)
                    .orElse(null),
                commentRepository.findLatestCommentOrderByModifiedDate(username)
                    .map(MutableBaseEntity::getModifiedAt)
                    .orElse(null)
            )
            .filter(Objects::nonNull)
            .max(LocalDateTime::compareTo)
            .orElse(null);

        return latest == null || isDifferenceMoreThanOneMinute(latest);
    }

    private boolean isDifferenceMoreThanOneMinute(LocalDateTime localDateTime) {
        LocalDateTime now = new Date().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        Duration duration = Duration.between(localDateTime, now);
        return Math.abs(duration.toMinutes()) > 1;
    }
}
