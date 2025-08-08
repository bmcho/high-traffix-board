package com.bmcho.hightrafficboard.repository;

import com.bmcho.hightrafficboard.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
}
