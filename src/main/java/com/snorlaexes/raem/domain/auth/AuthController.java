package com.snorlaexes.raem.domain.auth;

import com.snorlaexes.raem.global.apiPayload.ApiResponse;
import com.snorlaexes.raem.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/signup")
    public ApiResponse<AuthResDTO.SignUpResponseDTO> signUp(@RequestBody AuthReqDTO.SignUpReqDTO req) {
        UserEntity user = authService.createUser(req);
        return ApiResponse.onSuccess(AuthResDTO.SignUpResponseDTO.signUpResultDTO(user));
    }

    @PostMapping("/signin")
    public ApiResponse<AuthResDTO.SignInResponseDTO> signIn(@RequestBody AuthReqDTO.SignInReqDTO req) {
        return ApiResponse.onSuccess(AuthResDTO.SignInResponseDTO.signInResponseDTO(authService.signIn(req)));
    }
}
