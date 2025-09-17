package com.bmcho.hightrafficboard.entity;


import com.bmcho.hightrafficboard.entity.audit.MutableBaseEntity;
import com.bmcho.hightrafficboard.entity.redis.HotArticle;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "articles")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleEntity extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserEntity author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private BoardEntity board;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private List<CommentEntity> comments = new ArrayList<>();

    @Column(nullable = false)
    private Long viewCount = 0L;

    public ArticleEntity(UserEntity author, BoardEntity board, String title, String content) {
        this.author = author;
        this.board = board;
        this.title = title;
        this.content = content;
        this.comments = null;
    }

    public static ArticleEntity fromHotArticle(HotArticle hotArticle) {
        ArticleEntity entity = new ArticleEntity();
        entity.setId(hotArticle.getId());
        entity.setTitle(hotArticle.getTitle());
        entity.setContent(hotArticle.getContent());
        entity.setAuthor(hotArticle.getAuthor());
        entity.setCreatedAt(hotArticle.getCreatedAt());
        entity.setModifiedAt(hotArticle.getModifiedAt());
        entity.setViewCount(hotArticle.getViewCount());
        entity.setIsDeleted(false);
        return entity;
    }
}
