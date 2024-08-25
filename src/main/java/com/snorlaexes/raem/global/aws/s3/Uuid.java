package com.snorlaexes.raem.global.aws.s3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Uuid")
public class Uuid {
    @Id
    String id;
    String uuid;
    @CreatedDate
    LocalDateTime createdAt;
}
