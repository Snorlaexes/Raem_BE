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
    public ApiResponse<AuthResponse.SignUpResponseDTO> signUp(@RequestBody AuthRequest.SignUpReqDTO req) {
        UserEntity user = authService.createUser(req);
        return ApiResponse.onSuccess(AuthResponse.SignUpResponseDTO.signUpResultDTO(user));
    }

    @PostMapping("/signin")
    public ApiResponse<AuthResponse.SignInResponseDTO> signIn(@RequestBody AuthRequest.SignInReqDTO req) {
        return ApiResponse.onSuccess(AuthResponse.SignInResponseDTO.signInResponseDTO(authService.signIn(req)));
    }
}
