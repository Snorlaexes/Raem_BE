package com.snorlaexes.raem.domain.sleep.entities;

import com.snorlaexes.raem.domain.user.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "SleepDataUrl")
public class SleepDataUrlEntity {
    @Id
    String id;
    String url;
    @CreatedDate
    LocalDateTime createdAt;

    @DBRef
    private UserEntity user;
}
