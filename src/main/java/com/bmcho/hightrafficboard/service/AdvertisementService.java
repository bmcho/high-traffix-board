
package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.controller.advertisement.dto.WriteAdRequest;
import com.bmcho.hightrafficboard.entity.AdvertisementEntity;
import com.bmcho.hightrafficboard.repository.AdvertisementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
    private static final String REDIS_KEY = "ad:";

    private final AdvertisementRepository advertisementRepository;
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

    public AdvertisementEntity getAd(Long adId) {
        Object tempObj = redisTemplate.opsForHash().get(REDIS_KEY, adId);
        if (tempObj != null) {
            return (AdvertisementEntity) tempObj;
        }
        return advertisementRepository.findById(adId).orElse(null);
    }
}