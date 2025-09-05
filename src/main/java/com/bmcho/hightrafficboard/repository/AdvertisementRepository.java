package com.bmcho.hightrafficboard.repository;

import com.bmcho.hightrafficboard.entity.AdvertisementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertisementRepository  extends JpaRepository<AdvertisementEntity, Long> {
}