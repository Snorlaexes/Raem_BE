package com.snorlaexes.raem.domain.sleep.repository;

import com.snorlaexes.raem.domain.sleep.entities.SleepDataEntity;
import com.snorlaexes.raem.domain.user.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.Optional;

public interface SleepDataRepository extends MongoRepository<SleepDataEntity, String> {
    Optional<SleepDataEntity> findBySleptAtAndUser(Date date, UserEntity user);
}
