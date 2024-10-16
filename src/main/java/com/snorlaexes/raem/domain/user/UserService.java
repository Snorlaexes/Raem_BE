package com.snorlaexes.raem.domain.user;

import com.snorlaexes.raem.domain.user.verifyCode.VerificationCodeEntity;
import com.snorlaexes.raem.domain.user.verifyCode.VerificationCodeRepository;
import com.snorlaexes.raem.global.apiPayload.code.status.ErrorStatus;
import com.snorlaexes.raem.global.apiPayload.exception.ExceptionHandler;
import com.snorlaexes.raem.global.aws.s3.AmazonS3Manager;
import com.snorlaexes.raem.global.aws.s3.Uuid;
import com.snorlaexes.raem.global.aws.s3.UuidRepository;
import com.snorlaexes.raem.global.config.jwt.TokenEntity;
import com.snorlaexes.raem.global.config.jwt.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AmazonS3Manager amazonS3Manager;
    private final UuidRepository uuidRepository;

    @Transactional
    public UserEntity updateUserName(String userId, UserReqDTO req) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        String newName = req.username;

        user.setUsername(newName);
        user.setUpdated_at(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Transactional
    public UserEntity updateEmail(String userId, UserReqDTO req) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        String newEmail = req.getNewEmail();

        //코드 확인
        VerificationCodeEntity codeEntity = verificationCodeRepository.findByUser(user)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._CODE_NOT_EXIST));

        if (req.code != null && codeEntity.getVerifyCode() != req.code) {
            throw new ExceptionHandler(ErrorStatus._WRONG_CODE);
        }
        verificationCodeRepository.delete(codeEntity);

        user.setEmail(newEmail);
        user.setUpdated_at(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Transactional
    public void updatePassword(String userId, UserReqDTO req) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        String currentPassword = req.getCurrentPassword();
        String newPassword = req.getNewPassword();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ExceptionHandler(ErrorStatus._WRONG_PASSWORD);
        }

        //비밀번호 암호화
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        user.setPassword(encodedNewPassword);
        user.setUpdated_at(LocalDateTime.now());

        userRepository.save(user);
    }

    @Transactional
    public void logout(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        TokenEntity.RefreshToken refreshToken = tokenRepository.findByUserId(user.getId());

        tokenRepository.delete(refreshToken);
    }

    @Transactional
    public void drawOut(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        //토큰이 있으면 rtk 삭제(로그아웃 처리)
        TokenEntity.RefreshToken refreshToken = tokenRepository.findByUserId(userId);
        if (refreshToken != null) {
            tokenRepository.delete(refreshToken);
        }

        //유저 삭제
        userRepository.delete(user);
    }

    @Transactional
    public UserEntity retrieveUser(String userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));
    }

    @Transactional
    public void updateProfileImage(String userId, MultipartFile image) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());

        //이미 프사가 있으면 s3에서 지우기
        if (user.getImageUrl() != null){
            String fileKeyName = user.getImageUrl().split("com/")[1];
            amazonS3Manager.deleteFile(fileKeyName);
        }

        String imageUrl = amazonS3Manager.uploadFile("profile/" + savedUuid.getUuid(), image); // 이미지 업로드하기
        user.setImageUrl(imageUrl); // 프로필 이미지 변경
        userRepository.save(user);
    }
}
