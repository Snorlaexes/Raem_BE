package com.snorlaexes.raem.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserResDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserNameResponseDTO {
        String username;
        LocalDateTime updatedAt;

        public static UserResDTO.UpdateUserNameResponseDTO updateUserNameResultDTO(UserEntity user) {
            return UpdateUserNameResponseDTO.builder()
                    .username(user.getUsername())
                    .updatedAt(user.getUpdated_at())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessResponseDTO {
        LocalDateTime processedAt;

        public static UserResDTO.ProcessResponseDTO processResultDTO() {
            return ProcessResponseDTO.builder()
                    .processedAt(LocalDateTime.now())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateEmailResponseDTO {
        String email;
        LocalDateTime updatedAt;

        public static UserResDTO.UpdateEmailResponseDTO updateEmailResultDTO(UserEntity user) {
            return UpdateEmailResponseDTO.builder()
                    .email(user.getEmail())
                    .updatedAt(user.getUpdated_at())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetUserDataResponseDTO {
        private String username;
        private String email;
        private String imageUrl;
        private LocalDateTime created_at;

        public static GetUserDataResponseDTO getUserDataResponseDTO(UserEntity user) {
            return GetUserDataResponseDTO.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .imageUrl(user.getImageUrl())
                    .created_at(user.getCreated_at())
                    .build();
        }
    }
}
