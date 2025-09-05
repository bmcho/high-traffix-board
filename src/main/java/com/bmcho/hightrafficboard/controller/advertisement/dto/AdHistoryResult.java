package com.bmcho.hightrafficboard.controller.advertisement.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdHistoryResult {
    private Long adId;
    private Long count;
}