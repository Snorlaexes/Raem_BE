package com.snorlaexes.raem.domain.sleep.entities;

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

@Getter
@Setter
@Builder
@Document(collection = "Insights")
public class InsightEntity {
    @Id
    String id;
    String tag;

    String sleepPattern;
    String improvement;

    @DBRef
    UserEntity user;

    @CreatedDate
    LocalDateTime createdAt;
    @LastModifiedDate
    LocalDateTime updatedAt;
}
