package com.bmcho.hightrafficboard.repository;

import com.bmcho.hightrafficboard.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("SELECT c FROM CommentEntity c JOIN c.author u WHERE u.username = :username ORDER BY c.createdAt DESC LIMIT 1")
    CommentEntity findLatestCommentOrderByCreatedDate(@Param("username") String username);

    @Query("SELECT c FROM CommentEntity c JOIN c.author u WHERE u.username = :username ORDER BY c.modifiedAt DESC LIMIT 1")
    CommentEntity findLatestCommentOrderByUpdatedDate(@Param("username") String username);

    @Query("SELECT c FROM CommentEntity c WHERE c.article.id = :articleId AND c.isDeleted = false")
    List<CommentEntity> findByArticleId(@Param("articleId") Long articleId);

}
