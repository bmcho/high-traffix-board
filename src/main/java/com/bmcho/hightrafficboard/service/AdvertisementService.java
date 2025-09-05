
package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.config.security.BoardUser;
import com.bmcho.hightrafficboard.controller.advertisement.dto.WriteAdRequest;
import com.bmcho.hightrafficboard.entity.AdvertisementEntity;
import com.bmcho.hightrafficboard.entity.mongo.AdClickHistory;
import com.bmcho.hightrafficboard.entity.mongo.AdViewHistory;
import com.bmcho.hightrafficboard.entity.mongo.BaseHistory;
import com.bmcho.hightrafficboard.repository.AdvertisementRepository;
import com.bmcho.hightrafficboard.repository.mongo.AdClickHistoryRepository;
import com.bmcho.hightrafficboard.repository.mongo.AdViewHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
    private static final String REDIS_KEY = "ad:";

    private final AdvertisementRepository advertisementRepository;
    private final AdViewHistoryRepository adViewHistoryRepository;
    private final AdClickHistoryRepository adClickHistoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public AdvertisementEntity writeAd(WriteAdRequest dto) {
        AdvertisementEntity adEntity = new AdvertisementEntity(
            dto.getTitle(),
            dto.getContent(),
            dto.getIsDeleted(),
            dto.getIsVisible(),
            dto.getStartDate(),
            dto.getEndDate(),
            dto.getViewCount(),
            dto.getClickCount()
        );
        advertisementRepository.save(adEntity);
        redisTemplate.opsForHash().put(REDIS_KEY + adEntity.getId(), adEntity.getId(), adEntity);
        return adEntity;
    }

    public List<AdvertisementEntity> getAdList() {
        return advertisementRepository.findAll();
    }

    public AdvertisementEntity getAd(Long adId, String clientIp, Boolean isTrueView) {
        this.insertAdViewHistory(adId, clientIp, isTrueView);
        Object tempObj = redisTemplate.opsForHash().get(REDIS_KEY, adId);
        if (tempObj != null) {
            return (AdvertisementEntity) tempObj;
        }
        return advertisementRepository.findById(adId).orElse(null);
    }

    public void clickAd(Long adId, String clientIp) {
        saveHistory(() -> {
            AdClickHistory history = new AdClickHistory();
            history.setAdId(adId);
            history.setClientIp(clientIp);
            return history;
        }, adClickHistoryRepository::save);
    }

    public void insertAdViewHistory(Long adId, String clientIp, Boolean isTrueView) {
        saveHistory(() -> {
            AdViewHistory history = new AdViewHistory();
            history.setAdId(adId);
            history.setClientIp(clientIp);
            history.setIsTrueView(isTrueView);
            return history;
        }, adViewHistoryRepository::save);
    }

    // 공통 히스토리 저장 함수
    private <T extends BaseHistory> void saveHistory(Supplier<T> supplier, Consumer<T> repositorySave) {
        T history = supplier.get();
        Optional.ofNullable(getUsername()).ifPresent(history::setUsername);
        history.setCreatedDate(LocalDateTime.now());
        repositorySave.accept(history);
    }

    // 로그인 여부 체크 후 username 반환
    private String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        return (principal instanceof BoardUser user) ? user.getUsername() : null;
    }

}