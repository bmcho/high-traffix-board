
package com.bmcho.hightrafficboard.task;

import com.bmcho.hightrafficboard.controller.advertisement.dto.AdHistoryResult;
import com.bmcho.hightrafficboard.service.AdvertisementBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DailyStatTasks {
    private final AdvertisementBatchService advertisementBatchService;

    @Scheduled(cron = "00 00 00 * * ?")
    public void insertAdViewStatAtMidnight() {
        List<AdHistoryResult> viewResult = advertisementBatchService.getAdViewHistoryGroupedByAdId();
        advertisementBatchService.insertAdViewStat(viewResult);
        List<AdHistoryResult> clickResult = advertisementBatchService.getAdClickHistoryGroupedByAdId();
        advertisementBatchService.insertAdClickStat(clickResult);
    }
}