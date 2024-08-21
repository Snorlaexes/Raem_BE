package com.snorlaexes.raem.domain.sleep.entities;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SleepDataChildEntity {
    String state;
    LocalDateTime startTime;
    LocalDateTime endTime;
}
