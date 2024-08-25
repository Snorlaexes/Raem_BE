package com.snorlaexes.raem.domain.user;

import com.snorlaexes.raem.global.apiPayload.ApiResponse;
import com.snorlaexes.raem.global.apiPayload.code.status.ErrorStatus;
import com.snorlaexes.raem.global.apiPayload.exception.ExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PatchMapping
    public ApiResponse<?> updateUserData(@RequestParam String target, @RequestParam(required = false) Integer step, @RequestBody UserReqDTO req, @AuthenticationPrincipal String userId) {
        if (Objects.equals(target, "name")) {
            UserEntity user = userService.updateUserName(userId, req);
            return ApiResponse.onSuccess(UserResDTO.UpdateUserNameResponseDTO.updateUserNameResultDTO(user));

        } else if (Objects.equals(target, "email") && step == 1) { //인증 메일 전송
            userService.sendAuthenticationEmail(userId, req);
            return ApiResponse.onSuccess(UserResDTO.ProcessResponseDTO.processResultDTO());

        } else if (Objects.equals(target, "email") && step == 2) { //인증 코드 확인 후 이메일 변경
            UserEntity user = userService.updateEmail(userId, req);
            return ApiResponse.onSuccess(UserResDTO.UpdateEmailResponseDTO.updateEmailResultDTO(user));

        } else if (Objects.equals(target, "pw")) {
            userService.updatePassword(userId, req);
            return ApiResponse.onSuccess(UserResDTO.ProcessResponseDTO.processResultDTO());

        } else {
            throw new ExceptionHandler(ErrorStatus._BAD_REQUEST);
        }
    }

    @GetMapping("/logout")
    public ApiResponse<UserResDTO.ProcessResponseDTO> logout(@AuthenticationPrincipal String userId) {
        userService.logout(userId);
        return ApiResponse.onSuccess(UserResDTO.ProcessResponseDTO.processResultDTO());
    }


    @DeleteMapping("/drawout")
    public ApiResponse<UserResDTO.ProcessResponseDTO> drawOut(@AuthenticationPrincipal String userId) {
        userService.drawOut(userId);
        return ApiResponse.onSuccess(UserResDTO.ProcessResponseDTO.processResultDTO());
    }

    @GetMapping("/test")
    public ApiResponse<UserResDTO.ProcessResponseDTO> test(@AuthenticationPrincipal String userId) {
        userService.expireTest(userId);
        return ApiResponse.onSuccess(UserResDTO.ProcessResponseDTO.processResultDTO());
    }

    @GetMapping("/data")
    public ApiResponse<UserResDTO.GetUserDataResponseDTO> getUserData(@AuthenticationPrincipal String userId) {
        return ApiResponse.onSuccess(UserResDTO.GetUserDataResponseDTO.getUserDataResponseDTO(userService.retrieveUser(userId)));
    }

    @PatchMapping(value = "/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<?> updateProfileImage(@AuthenticationPrincipal String userId, @RequestPart MultipartFile image) {
        userService.updateProfileImage(userId, image);
        return ApiResponse.onSuccess(UserResDTO.ProcessResponseDTO.processResultDTO());
    }
}
