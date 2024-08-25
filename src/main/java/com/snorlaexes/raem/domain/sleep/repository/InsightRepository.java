package com.snorlaexes.raem.domain.sleep.repository;

import com.snorlaexes.raem.domain.sleep.entities.InsightEntity;
import com.snorlaexes.raem.domain.user.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InsightRepository extends MongoRepository<InsightEntity, String> {
    InsightEntity findByUserAndTag(UserEntity user, String tag);
}
