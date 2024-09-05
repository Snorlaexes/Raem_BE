package com.snorlaexes.raem.domain.sleep;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class SleepReqDTO {
    @Getter
    public static class SaveDataDTO {
        @NotNull
        LocalDateTime sleptAt; //'지금 취침하기'를 누른 순간
        @NotNull
        Integer score;
        @NotNull
        LocalTime awakeAt; //실제 기상 시간
        @NotNull
        LocalTime fellAsleepAt; //잠든 시점(DreamAI가 '잔다'고 판단한 그때)
        @NotNull
        LocalTime rem; //렘 인터벌
        @NotNull
        LocalTime sleepTime; //총 수면 시간(렘+딥+코어)
        /*@Nullable
        SleepData sleepData;*/
    }

    /*@Getter
    public static class SleepData {
        LocalTime Rem;
        LocalTime Deep;
        LocalTime Core;
    }*/

    @Getter
    public static class SaveReasonDTO {
        @NotNull
        String sleepDataId;
        @NotNull
        String reason;
    }

    @Getter
    public static class GetDataUrlDTO {
        @NotNull
        String dataId;
    }
}
