package com.snorlaexes.raem.domain.sleep;

import com.snorlaexes.raem.domain.sleep.entities.SleepDataEntity;
import com.snorlaexes.raem.domain.sleep.entities.SleepDataUrlEntity;
import lombok.*;

import java.time.LocalDateTime;

public class SleepResDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveDataResponseDTO {
        String dataId;
        LocalDateTime createdAt;

        public static SaveDataResponseDTO saveDataResultDTO (SleepDataUrlEntity entity){
            return SaveDataResponseDTO.builder()
                    .dataId(entity.getId())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }

        public static SaveDataResponseDTO organizeDataResultDTO (SleepDataEntity entity) {
            return SaveDataResponseDTO.builder()
                    .dataId(entity.getId())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveReasonResponseDTO {
        LocalDateTime updatedAt;

        public static SaveReasonResponseDTO saveReasonResultDTO (SleepDataEntity entity){
            return SaveReasonResponseDTO.builder()
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetDataUrlResponseDTO {
        String url;

        public static GetDataUrlResponseDTO getDataUrlResponseDTO (SleepDataUrlEntity entity){
            return GetDataUrlResponseDTO.builder()
                    .url(entity.getUrl())
                    .build();
        }
    }
}
