package com.bmcho.hightrafficboard.repository.mongo;

import com.bmcho.hightrafficboard.entity.mongo.AdViewHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdViewHistoryRepository extends MongoRepository<AdViewHistory, String> {
}