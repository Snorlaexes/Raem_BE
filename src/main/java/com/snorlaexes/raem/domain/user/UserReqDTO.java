package com.snorlaexes.raem.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReqDTO {
    @Nullable
    String username;
    @Nullable
    String currentPassword;
    @Nullable
    String newPassword;
    @Nullable
    String newEmail;
    @Nullable
    Integer code;
}
