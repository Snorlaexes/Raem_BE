package com.snorlaexes.raem.domain.sleep.repository;

import com.snorlaexes.raem.domain.sleep.entities.AnalysisDataEntity;
import com.snorlaexes.raem.domain.user.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AnalysisDataRepository extends MongoRepository<AnalysisDataEntity, String> {
    AnalysisDataEntity findByUserAndTag(UserEntity user, String tag);
    List<AnalysisDataEntity> findByUserAndTagContaining(UserEntity user, String tagPart);
}
