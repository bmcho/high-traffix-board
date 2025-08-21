package com.bmcho.hightrafficboard.repository;

import com.bmcho.hightrafficboard.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {

    @Query("SELECT a FROM ArticleEntity  a WHERE a.board.id = :boardId AND a.isDeleted = false ORDER BY a.createdAt DESC LIMIT 10")
    List<ArticleEntity> findTop10ByBoardIdOrderByCreatedAtDesc(@Param("boardId") long boardId);

    @Query("SELECT a FROM ArticleEntity  a WHERE a.board.id = :boardId AND a.id < :articleId AND a.isDeleted = false ORDER BY a.createdAt DESC LIMIT 10")
    List<ArticleEntity> findTop10ByBoardIdAndArticleIdLessThanOrderByCreatedAtDesc(@Param("boardId") Long boardId,
                                                                                     @Param("articleId") Long articleId);

    @Query("SELECT a FROM ArticleEntity  a WHERE a.board.id = :boardId AND a.id > :articleId AND a.isDeleted = false ORDER BY a.createdAt DESC LIMIT 10")
    List<ArticleEntity> findTop10ByBoardIdAndArticleIdGreaterThanOrderByCreatedAtDesc(@Param("boardId") Long boardId,
                                                                                        @Param("articleId") Long articleId);

    @Query("SELECT a FROM ArticleEntity a JOIN a.author u WHERE u.username = :username ORDER BY a.createdAt DESC LIMIT 1")
    Optional<ArticleEntity> findLatestArticleByAuthorUsernameOrderByCreatedAt(@Param("username") String username);

    @Query("SELECT a FROM ArticleEntity a JOIN a.author u WHERE u.username = :username ORDER BY a.modifiedAt DESC LIMIT 1")
    Optional<ArticleEntity> findLatestArticleByAuthorUsernameOrderByModifiedAt(@Param("username") String username);

}
