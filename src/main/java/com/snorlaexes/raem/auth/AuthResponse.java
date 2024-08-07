package com.snorlaexes.raem.auth;

import com.snorlaexes.raem.user.UserEntity;
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
}
