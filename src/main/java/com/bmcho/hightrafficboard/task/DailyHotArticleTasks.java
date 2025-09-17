package com.bmcho.hightrafficboard.task;

import com.bmcho.hightrafficboard.entity.ArticleEntity;
import com.bmcho.hightrafficboard.entity.redis.HotArticle;
import com.bmcho.hightrafficboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DailyHotArticleTasks {

    public static final String YESTERDAY_REDIS_KEY = "yesterday-hot-article:";
    public static final String WEEK_REDIS_KEY = "week-hot-article:";

    private final ArticleRepository articleRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Scheduled(cron = "00 00 00 * * ?")
    public void pickYesterdayHotArticle() {
        LocalDateTime startDate = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        articleRepository.findHotArticle(startDate, endDate)
            .ifPresent(entity -> {
                HotArticle hotArticle = HotArticle.fromEntity(entity);
                redisTemplate.opsForHash().put(YESTERDAY_REDIS_KEY + hotArticle.getId(), hotArticle.getId(), hotArticle);
            });
    }

    @Scheduled(cron = "20 28 07 * * ?")
    public void pickWeekHotArticle() {
        LocalDateTime startDate = LocalDateTime.of(LocalDate.now().minusDays(8), LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN);
        articleRepository.findHotArticle(startDate, endDate)
            .ifPresent(entity -> {
                HotArticle hotArticle = HotArticle.fromEntity(entity);
                redisTemplate.opsForHash().put(WEEK_REDIS_KEY + hotArticle.getId(), hotArticle.getId(), hotArticle);
            });
    }
}