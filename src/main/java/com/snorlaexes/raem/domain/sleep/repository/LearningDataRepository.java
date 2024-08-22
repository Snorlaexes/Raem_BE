package com.snorlaexes.raem.domain.sleep.repository;

import com.snorlaexes.raem.domain.sleep.entities.LearningDataEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LearningDataRepository extends MongoRepository<LearningDataEntity, String> {
}
