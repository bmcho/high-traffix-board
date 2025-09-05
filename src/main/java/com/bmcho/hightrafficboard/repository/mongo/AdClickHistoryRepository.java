package com.bmcho.hightrafficboard.repository.mongo;

import com.bmcho.hightrafficboard.entity.mongo.AdClickHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdClickHistoryRepository extends MongoRepository<AdClickHistory, String> {
}