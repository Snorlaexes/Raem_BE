package com.snorlaexes.raem.domain.sleep.repository;

import com.snorlaexes.raem.domain.sleep.entities.SleepDataEntity;
import com.snorlaexes.raem.domain.user.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface SleepDataRepository extends MongoRepository<SleepDataEntity, String> {
    List<SleepDataEntity> findAllByUserAndSleptAtBetween(UserEntity user, LocalDate first, LocalDate last);
    List<SleepDataEntity> findAllByUserAndScoreGreaterThan(UserEntity user, Integer score);
}
