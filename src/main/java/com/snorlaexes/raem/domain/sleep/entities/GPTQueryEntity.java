package com.snorlaexes.raem.domain.sleep.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Builder
public class GPTQueryEntity {
    String yearAndMonth;
    Float avgSleepScore;
    LocalTime avgAwakeTime;
    LocalTime avgInBedTime;
    LocalTime avgSleepTime;
    String sleepDisturbance;
}
