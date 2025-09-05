package com.bmcho.hightrafficboard.controller.advertisement.dto;

import com.bmcho.hightrafficboard.entity.AdvertisementEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdvertisementResponse {
    private long id;
    private String title;
    private String content;
    private boolean isDeleted;
    private boolean isVisible;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int viewCount;
    private int clickCount;

    public static AdvertisementResponse from(AdvertisementEntity entity) {
        return AdvertisementResponse.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .content(entity.getContent())
            .isDeleted(entity.getIsDeleted())
            .isVisible(entity.getIsVisible())
            .startDate(entity.getStartDate())
            .endDate(entity.getEndDate())
            .viewCount(entity.getViewCount())
            .clickCount(entity.getViewCount())
            .build();
    }
}
