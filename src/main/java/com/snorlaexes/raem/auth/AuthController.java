package com.snorlaexes.raem.auth;

import com.snorlaexes.raem.global.apiPayload.ApiResponse;
import com.snorlaexes.raem.user.UserEntity;
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
}
