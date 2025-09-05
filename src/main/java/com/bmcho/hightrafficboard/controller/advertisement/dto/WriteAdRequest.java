package com.bmcho.hightrafficboard.controller.advertisement.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WriteAdRequest {
    private String title;
    private String content;
    private Boolean isDeleted = false;
    private Boolean isVisible = true;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer viewCount = 0;
    private Integer clickCount = 0;
}
