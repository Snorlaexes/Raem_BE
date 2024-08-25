package com.snorlaexes.raem.domain.sleep.entities;

import com.snorlaexes.raem.domain.sleep.Enums.BadAwakeReason;
import com.snorlaexes.raem.domain.sleep.Enums.Range;
import com.snorlaexes.raem.domain.user.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Getter
@Setter
@Builder
@Document(collection = "AnalysisData")
public class AnalysisDataEntity {
    @Id
    String id;

    Range type;
    String tag;

    Integer scoreSum;
    LocalTime inBedSum;
    LocalTime awakeSum;
    LocalTime sleepTimeSum;
    Map<BadAwakeReason, Integer> badAwakeReasonsCount;
    Integer dataCount;

    @DBRef
    UserEntity user;

    @CreatedDate
    LocalDateTime createdAt;
    @LastModifiedDate
    LocalDateTime updatedAt;
}
