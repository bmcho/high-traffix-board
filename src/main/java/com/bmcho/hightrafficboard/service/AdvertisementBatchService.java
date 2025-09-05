
package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.controller.advertisement.dto.AdHistoryResult;
import com.bmcho.hightrafficboard.entity.AdClickStatEntity;
import com.bmcho.hightrafficboard.entity.AdViewStatEntity;
import com.bmcho.hightrafficboard.repository.AdClickStatRepository;
import com.bmcho.hightrafficboard.repository.AdViewStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementBatchService {

    private final AdViewStatRepository adViewStatRepository;
    private final AdClickStatRepository adClickStatRepository;
    private final MongoTemplate mongoTemplate;

    public List<AdHistoryResult> getAdViewHistoryGroupedByAdId() {
        return getAdHistoryGroupedByAdId("adViewHistory");
    }

    public List<AdHistoryResult> getAdClickHistoryGroupedByAdId() {
        return getAdHistoryGroupedByAdId("adClickHistory");
    }

    public void insertAdViewStat(List<AdHistoryResult> result) {
        saveStats(result, AdViewStatEntity::new, adViewStatRepository);
    }

    public void insertAdClickStat(List<AdHistoryResult> result) {
        saveStats(result, AdClickStatEntity::new, adClickStatRepository);
    }

    private List<AdHistoryResult> getAdHistoryGroupedByAdId(String collectionName) {
        List<AdHistoryResult> usernameResult = getAdHistoryGrouped(collectionName, "username", true);
        List<AdHistoryResult> clientIpResult = getAdHistoryGrouped(collectionName, "clientIp", false);

        List<AdHistoryResult> resultList = new ArrayList<>();
        resultList.addAll(usernameResult);
        resultList.addAll(clientIpResult);

        return resultList;
    }

    private List<AdHistoryResult> getAdHistoryGrouped(String collectionName, String field, boolean hasUsername) {
        LocalDateTime startOfDay = getStartOfYesterday();
        LocalDateTime endOfDay = getEndOfYesterday();

        MatchOperation matchStage = Aggregation.match(
            Criteria.where("createdAt").gte(startOfDay).lt(endOfDay)
                .and("username").exists(hasUsername)
        );

        GroupOperation groupStage = Aggregation.group("adId")
            .addToSet(field).as("uniqueValues");

        ProjectionOperation projectStage = Aggregation.project()
            .andExpression("_id").as("adId")
            .andExpression("size(uniqueValues)").as("count");

        Aggregation aggregation = Aggregation.newAggregation(matchStage, groupStage, projectStage);
        AggregationResults<AdHistoryResult> results =
            mongoTemplate.aggregate(aggregation, collectionName, AdHistoryResult.class);

        return results.getMappedResults();
    }

    private <T> void saveStats(List<AdHistoryResult> result, StatEntityFactory<T> factory, JpaRepository<T, ?> repository) {

        String formattedDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<T> entities = result.stream()
            .map(item -> factory.create(item.getAdId(), item.getCount(), formattedDate))
            .toList();
        repository.saveAll(entities);
    }

    private LocalDateTime getStartOfYesterday() {
        return LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN).plusHours(9);
    }

    private LocalDateTime getEndOfYesterday() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusHours(9);
    }

    @FunctionalInterface
    private interface StatEntityFactory<T> {
        T create(Long adId, Long count, String date);
    }

}