package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.config.security.BoardUser;
import com.bmcho.hightrafficboard.controller.comment.dto.WriteCommentRequest;
import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.entity.BoardEntity;
import com.bmcho.hightrafficboard.entity.CommentEntity;
import com.bmcho.hightrafficboard.entity.UserEntity;
import com.bmcho.hightrafficboard.exception.ArticleException;
import com.bmcho.hightrafficboard.exception.BoardException;
import com.bmcho.hightrafficboard.exception.UserException;
import com.bmcho.hightrafficboard.repository.ArticleRepository;
import com.bmcho.hightrafficboard.repository.BoardRepository;
import com.bmcho.hightrafficboard.repository.CommentRepository;
import com.bmcho.hightrafficboard.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final BoardRepository boardRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentEntity writeComment(Long boardId, Long articleId, WriteCommentRequest dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        BoardUser boardUser = (BoardUser) authentication.getPrincipal();

        if (!this.isCanWriteComment(boardUser.getUsername())) {
            throw new ArticleException.ArticleNotEditedByRateLimitException();
        }

        UserEntity author = userRepository.findByUsername(boardUser.getUsername())
            .orElseThrow(UserException.UserDoesNotExistException::new);
        BoardEntity board = boardRepository.findById(boardId)
            .orElseThrow(BoardException.BoardDoesNotExistException::new);
        ArticleEntity article = articleRepository.findById(articleId)
            .filter(a -> !a.getIsDeleted())
            .orElseThrow(ArticleException.ArticleDoesNotExistException::new);

        CommentEntity comment = new CommentEntity(
            dto.getContent(),
            author,
            article
        );
        commentRepository.save(comment);
        return comment;
    }

    private boolean isCanWriteComment(String username) {
        CommentEntity latestComment = commentRepository.findLatestCommentOrderByCreatedDate(username);
        if (latestComment == null) {
            return true;
        }
        return this.isDifferenceMoreThanOneMinutes(latestComment.getCreatedAt());
    }

    private boolean isCanEditComment(String username) {
        CommentEntity latestComment = commentRepository.findLatestCommentOrderByCreatedDate(username);
        if (latestComment == null) {
            return true;
        }
        return this.isDifferenceMoreThanOneMinutes(latestComment.getModifiedAt());
    }

    private boolean isDifferenceMoreThanOneMinutes(LocalDateTime localDateTime) {
        LocalDateTime dateAsLocalDateTime = new Date().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        Duration duration = Duration.between(localDateTime, dateAsLocalDateTime);

        return Math.abs(duration.toMinutes()) > 1;
    }
}
