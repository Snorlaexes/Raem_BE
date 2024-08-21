package com.snorlaexes.raem.global.config.jwt;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepository extends MongoRepository<TokenEntity.RefreshToken, String> {
    TokenEntity.RefreshToken findByUserId(String userId);
}
