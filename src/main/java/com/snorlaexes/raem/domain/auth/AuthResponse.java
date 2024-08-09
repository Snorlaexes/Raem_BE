package com.snorlaexes.raem.domain.auth;

import com.snorlaexes.raem.domain.user.UserEntity;
import com.snorlaexes.raem.global.config.jwt.TokenEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AuthResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpResponseDTO {
        String userId;
        LocalDateTime createdAt;

        public static SignUpResponseDTO signUpResultDTO(UserEntity user) {
            return SignUpResponseDTO.builder()
                    .userId(user.getId())
                    .createdAt(user.getCreated_at())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignInResponseDTO {
        String accessToken;
        String refreshToken;

        public static SignInResponseDTO signInResponseDTO(TokenEntity.Tokens tokens) {
            return SignInResponseDTO.builder()
                    .accessToken(tokens.getAccessToken())
                    .refreshToken(tokens.getRefreshToken())
                    .build();
        }
    }
}
