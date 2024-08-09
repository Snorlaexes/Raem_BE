package com.snorlaexes.raem.global.config.jwt;

import com.snorlaexes.raem.domain.user.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

public class TokenEntity {

    @Getter
    @Setter
    @Builder
    public static class Tokens {
        String accessToken;
        String refreshToken;
    }

    @Getter
    @Setter
    @Builder
    @Document(collection = "RefreshToken")
    public static class RefreshToken {
        @Id
        private String id;
        private String refreshToken;

        @DBRef
        private UserEntity user;
    }
}
