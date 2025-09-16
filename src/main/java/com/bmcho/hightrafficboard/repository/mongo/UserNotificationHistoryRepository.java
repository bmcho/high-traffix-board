package com.bmcho.hightrafficboard.repository.mongo;

import com.bmcho.hightrafficboard.entity.mongo.UserNotificationHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserNotificationHistoryRepository extends MongoRepository<UserNotificationHistory, String> {
}