package com.bmcho.hightrafficboard.entity;

import com.bmcho.hightrafficboard.entity.audit.MutableBaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "advertisements")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdvertisementEntity extends MutableBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content = "";

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column(nullable = false)
    private Boolean isVisible = true;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private Integer clickCount = 0;

    public AdvertisementEntity(String title, String content, Boolean isDeleted, Boolean isVisible,
                               LocalDateTime startDate, LocalDateTime endDate, Integer viewCount, Integer clickCount
    ) {
        this.title = title;
        this.content = content;
        this.isDeleted = isDeleted;
        this.isVisible = isVisible;
        this.startDate = startDate;
        this.endDate = endDate;
        this.viewCount = viewCount;
        this.clickCount = clickCount;
    }

}