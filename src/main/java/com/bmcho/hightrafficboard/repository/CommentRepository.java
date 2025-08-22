package com.bmcho.hightrafficboard.repository;

import com.bmcho.hightrafficboard.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("SELECT c FROM CommentEntity c JOIN c.author u WHERE u.username = :username " +
        "AND c.isDeleted = false AND c.article.isDeleted = false ORDER BY c.createdAt DESC LIMIT 1")
    Optional<CommentEntity> findLatestCommentOrderByCreatedDate(@Param("username") String username);

    @Query("SELECT c FROM CommentEntity c JOIN c.author u WHERE u.username = :username " +
        "AND c.isDeleted = false AND c.article.isDeleted = false ORDER BY c.modifiedAt DESC LIMIT 1")
    Optional<CommentEntity> findLatestCommentOrderByModifiedDate(@Param("username") String username);

    @Query("SELECT c FROM CommentEntity c WHERE c.article.id = :articleId AND c.isDeleted = false AND c.article.isDeleted = false")
    List<CommentEntity> findByArticleId(@Param("articleId") Long articleId);

}
