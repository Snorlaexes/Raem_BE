package com.snorlaexes.raem.domain.sleep.entities;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InBedEntity {
    LocalDateTime startTime;
    LocalDateTime endTime;
}
