package com.snorlaexes.raem.domain.sleep;

import com.snorlaexes.raem.domain.sleep.entities.InBedEntity;
import com.snorlaexes.raem.domain.sleep.entities.SleepDataChildEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SleepReqDTO {
    @Getter
    public static class SaveDataDTO {
        @NotNull
        LocalDate sleptAt;
        @Nullable
        Integer score;
        @Nullable
        LocalTime setTime;
        @Nullable
        LocalTime awakeAt;
        @Nullable
        TotalSleepDataChild totalSleepData;
    }

    @Getter
    public static class TotalSleepDataChild {
        List<InBedEntity> inBed;
        List<SleepDataChildEntity> sleepData;
    }

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
