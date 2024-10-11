package com.snorlaexes.raem.domain.sleep.repository;

import com.snorlaexes.raem.domain.sleep.entities.SleepDataUrlEntity;
import com.snorlaexes.raem.domain.user.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SleepDataUrlRepository extends MongoRepository<SleepDataUrlEntity, String> {
    SleepDataUrlEntity findByUserAndType(UserEntity user, String type);

    @Query("{ 'user.$_id': ?0, 'type': ?1, 'createdAt': { $gte: ?2, $lt: ?3 } }")
    Optional<SleepDataUrlEntity> findByUserAndTypeAndCreatedAt(String userId, String type, Date startDate, Date endDate);
}
