package com.bmcho.hightrafficboard.entity.mongo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public abstract class BaseHistory {
    private Long adId;
    private String clientIp;
    private String username;
    private LocalDateTime createdDate;
}