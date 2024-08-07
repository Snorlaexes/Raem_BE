package com.snorlaexes.raem.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class AuthRequest {

    @Getter
    public static class SignUpReqDTO {
        @NotNull
        String username;
        @NotNull
        String email;
        @NotNull
        String password;
    }
}
