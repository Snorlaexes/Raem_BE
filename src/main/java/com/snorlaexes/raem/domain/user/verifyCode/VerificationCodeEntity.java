package com.snorlaexes.raem.domain.user.verifyCode;

import com.snorlaexes.raem.domain.user.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Builder
@Document("VerifyCode")
public class VerificationCodeEntity {
    @Id
    private String id;
    private int verifyCode;
    @Indexed(name = "expiredAt", expireAfterSeconds = 10)
    private Date expiredAt;
    @DBRef
    private UserEntity user;
}
