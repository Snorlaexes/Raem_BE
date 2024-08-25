package com.snorlaexes.raem.global.aws.s3;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UuidRepository extends MongoRepository<Uuid, String> {
}
