package com.bmcho.hightrafficboard.repository;

import com.bmcho.hightrafficboard.entity.AdClickStatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdClickStatRepository extends JpaRepository<AdClickStatEntity, Long> {
}