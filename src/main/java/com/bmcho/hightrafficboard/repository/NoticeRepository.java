package com.bmcho.hightrafficboard.repository;

import com.bmcho.hightrafficboard.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {
    @Query("SELECT n FROM NoticeEntity n WHERE n.createdAt >= :startDate ORDER BY n.createdAt")
    List<NoticeEntity> findByCreatedDate(@Param("startDate") LocalDateTime startDate);
}