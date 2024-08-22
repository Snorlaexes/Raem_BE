package com.snorlaexes.raem.domain.sleep.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "LearningData")
public class LearningDataEntity {
    @Id
    String id;
    String tag;
    String url;
    @CreatedDate
    LocalDateTime createdAt;
}
