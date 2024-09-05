package com.snorlaexes.raem.domain.sleep.entities;

import com.snorlaexes.raem.domain.sleep.Enums.BadAwakeReason;
import com.snorlaexes.raem.domain.user.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@Document(collection = "SleepData")
public class SleepDataEntity {
    @Id
    String id;
    LocalDate sleptAt;
    Integer score;
    BadAwakeReason badAwakeReason;

    LocalTime awakeTime;
    LocalTime fellAsleepTime;
    LocalTime sleepTime;
    LocalTime timeOnBed;

    LocalTime rem;

    @CreatedDate
    LocalDateTime createdAt;
    @LastModifiedDate
    LocalDateTime updatedAt;

    @DBRef
    private UserEntity user;
}
