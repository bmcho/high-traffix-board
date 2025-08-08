package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.config.security.BoardUser;
import com.bmcho.hightrafficboard.controller.Article.dto.WriteArticleRequest;
import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.entity.BoardEntity;
import com.bmcho.hightrafficboard.entity.UserEntity;
import com.bmcho.hightrafficboard.exception.BoardException;
import com.bmcho.hightrafficboard.exception.UserException;
import com.bmcho.hightrafficboard.repository.ArticleRepository;
import com.bmcho.hightrafficboard.repository.BoardRepository;
import com.bmcho.hightrafficboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public ArticleEntity writeArticle(WriteArticleRequest dto) {
        // filter 에서 검증된 유저, 예외처리 x
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        BoardUser boardUser = (BoardUser) authentication.getPrincipal();

        UserEntity author = userRepository.findById(boardUser.getId())
            .orElseThrow(UserException.UserDoesNotExistException::new);

        BoardEntity board = boardRepository.findById(dto.getBoardId())
            .orElseThrow(BoardException.BoardDoesNotExistException::new);

        ArticleEntity article = new ArticleEntity(
            author,
            board,
            dto.getTitle(),
            dto.getContent()
        );

        return articleRepository.save(article);
    }

}
