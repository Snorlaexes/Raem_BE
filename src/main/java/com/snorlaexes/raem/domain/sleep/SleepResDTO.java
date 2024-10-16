package com.snorlaexes.raem.domain.sleep;

import com.snorlaexes.raem.domain.sleep.Enums.BadAwakeReason;
import com.snorlaexes.raem.domain.sleep.Enums.Range;
import com.snorlaexes.raem.domain.sleep.entities.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SleepResDTO {
    private static String timeStringGenerator(LocalTime dataTime) {
        int hours = dataTime.getHour();
        int minutes = dataTime.getMinute();

        if (hours > 0) {
            return hours + "시간 " + minutes + "분";
        } else {
            return minutes + "분";
        }
    }

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

        public static SaveDataResponseDTO saveLearningDataResultDTO (LearningDataEntity entity) {
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
    public static class UpdateURLResponseDTO {
        String dataId;
        LocalDateTime updatedAt;

        public static UpdateURLResponseDTO updateURLResultDTO (SleepDataUrlEntity entity){
            return UpdateURLResponseDTO.builder()
                    .dataId(entity.getId())
                    .updatedAt(entity.getUpdatedAt())
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
    public static class GetDataUrlListResponseDTO {
        List<GetDataUrlResponseDTO> list;

        public static GetDataUrlListResponseDTO getDataUrlListResponseDTO (List<SleepDataUrlEntity> entities){
            List<GetDataUrlResponseDTO> list = entities.stream()
                    .map(GetDataUrlResponseDTO::getDataUrlResponseDTO).toList();

            return GetDataUrlListResponseDTO.builder()
                    .list(list)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetDataUrlResponseDTO {
        String type;
        String url;

        public static GetDataUrlResponseDTO getDataUrlResponseDTO (SleepDataUrlEntity entity){
            return GetDataUrlResponseDTO.builder()
                    .type(entity.getType())
                    .url(entity.getUrl())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetWeeklyDataDTO {
        Range type;
        List<GetDailyDataDTO> list;

        public static GetWeeklyDataDTO getWeeklyDataDTO(List<SleepDataEntity> entityList) {
            List<GetDailyDataDTO> convertList = entityList.stream()
                    .map(GetDailyDataDTO::getDailyDataDTO)
                    .toList();

            return GetWeeklyDataDTO.builder()
                    .type(Range.Weekly)
                    .list(convertList)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetDailyDataDTO {
        String dataId;
        LocalDate sleptAt;
        Integer score;
        BadAwakeReason badAwakeReason;
        String awakeTime;
        String fellAsleepTime;
        String sleepTime;
        String timeOnBed;

        public static GetDailyDataDTO getDailyDataDTO(SleepDataEntity entity) {
            String awakeTimeStr = entity.getAwakeTime().getHour() + "시 " + entity.getAwakeTime().getMinute() + "분";
            String fellAsleepTimeStr = timeStringGenerator(entity.getFellAsleepTime());
            String sleepTimeStr = timeStringGenerator(entity.getSleepTime());
            String timeOnBedStr = timeStringGenerator(entity.getTimeOnBed());

            return GetDailyDataDTO.builder()
                    .dataId(entity.getId())
                    .sleptAt(entity.getSleptAt())
                    .score(entity.getScore())
                    .badAwakeReason(entity.getBadAwakeReason())
                    .awakeTime(awakeTimeStr)
                    .fellAsleepTime(fellAsleepTimeStr)
                    .sleepTime(sleepTimeStr)
                    .timeOnBed(timeOnBedStr)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetOverMonthDataListDTO {
        String type;
        List<GetOverMonthDataDTO> dataList;

        public static GetOverMonthDataListDTO getOverMonthDataListDTO(String type, List<AnalysisDataEntity> entities) {
            List<GetOverMonthDataDTO> list = entities.stream().map(GetOverMonthDataDTO::getOverMonthDataDTO).toList();

            return GetOverMonthDataListDTO.builder()
                    .type(type)
                    .dataList(list)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetOverMonthDataDTO {
        String tag;
        Integer scoreSum;
        LocalTime inBedSum;
        LocalTime awakeSum;
        LocalTime sleepTimeSum;
        List<GetReasonsListDTO> badAwakeReasonsCount;
        Integer dataCount;

        public static GetOverMonthDataDTO getOverMonthDataDTO(AnalysisDataEntity entity) {
            List<GetReasonsListDTO> reasonsListDTOS = new ArrayList<>();
            reasonsListDTOS.add(GetReasonsListDTO.getReasonsListDTO(BadAwakeReason.COFFEE, entity.getBadAwakeReasonsCount().get(BadAwakeReason.COFFEE)));
            reasonsListDTOS.add(GetReasonsListDTO.getReasonsListDTO(BadAwakeReason.EXERCISE, entity.getBadAwakeReasonsCount().get(BadAwakeReason.EXERCISE)));
            reasonsListDTOS.add(GetReasonsListDTO.getReasonsListDTO(BadAwakeReason.STRESS, entity.getBadAwakeReasonsCount().get(BadAwakeReason.STRESS)));
            reasonsListDTOS.add(GetReasonsListDTO.getReasonsListDTO(BadAwakeReason.ALCOHOL, entity.getBadAwakeReasonsCount().get(BadAwakeReason.ALCOHOL)));
            reasonsListDTOS.add(GetReasonsListDTO.getReasonsListDTO(BadAwakeReason.SMARTPHONE, entity.getBadAwakeReasonsCount().get(BadAwakeReason.SMARTPHONE)));

            return GetOverMonthDataDTO.builder()
                    .tag(entity.getTag())
                    .scoreSum(entity.getScoreSum())
                    .inBedSum(entity.getInBedSum())
                    .awakeSum(entity.getAwakeSum())
                    .sleepTimeSum(entity.getSleepTimeSum())
                    .badAwakeReasonsCount(reasonsListDTOS)
                    .dataCount(entity.getDataCount())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetReasonsListDTO {
        BadAwakeReason reason;
        Integer count;

        public static GetReasonsListDTO getReasonsListDTO(BadAwakeReason reason, Integer count) {
            return GetReasonsListDTO.builder()
                    .reason(reason)
                    .count(count)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetInsightDTO {
        String sleepPattern;
        String improvement;

        public static GetInsightDTO getInsightDTO(InsightEntity insightEntity) {
            return GetInsightDTO.builder()
                    .sleepPattern(insightEntity.getSleepPattern())
                    .improvement(insightEntity.getImprovement())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetBestSleepTimeDTO {
        String bestTime;

        public static GetBestSleepTimeDTO getBestSleepTimeDTO(String bestTime) {
            return GetBestSleepTimeDTO.builder()
                    .bestTime(bestTime)
                    .build();
        }
    }
}
