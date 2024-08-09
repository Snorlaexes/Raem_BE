package com.snorlaexes.raem.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "User")
public class UserEntity {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String provider;
    @CreatedDate
    private LocalDateTime created_at;
    @LastModifiedDate
    private LocalDateTime updated_at;
}
