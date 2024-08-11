package com.snorlaexes.raem.domain.user.verifyCode;

import com.snorlaexes.raem.domain.user.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends MongoRepository<VerificationCodeEntity, String> {
    Optional<VerificationCodeEntity> findByUser(UserEntity user);
}
