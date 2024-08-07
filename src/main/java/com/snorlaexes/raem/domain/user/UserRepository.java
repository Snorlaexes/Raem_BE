package com.snorlaexes.raem.domain.user;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, String> {
    UserEntity findByEmail(String email);
}
