package com.snorlaexes.raem.domain.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class AuthReqDTO {

    @Getter
    public static class SignUpReqDTO {
        @NotNull
        String username;
        @NotNull
        String email;
        @NotNull
        String password;
    }

    @Getter
    public static class SignInReqDTO {
        @NotNull
        String email;
        @NotNull
        String password;
    }
}
