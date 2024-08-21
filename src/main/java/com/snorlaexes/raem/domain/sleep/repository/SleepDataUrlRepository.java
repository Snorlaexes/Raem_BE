package com.snorlaexes.raem.domain.sleep.repository;

import com.snorlaexes.raem.domain.sleep.entities.SleepDataUrlEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SleepDataUrlRepository extends MongoRepository<SleepDataUrlEntity, String> {
}
