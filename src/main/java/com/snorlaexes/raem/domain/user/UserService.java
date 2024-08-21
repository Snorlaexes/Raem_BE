package com.snorlaexes.raem.domain.user;

import com.snorlaexes.raem.domain.user.verifyCode.VerificationCodeEntity;
import com.snorlaexes.raem.domain.user.verifyCode.VerificationCodeRepository;
import com.snorlaexes.raem.global.apiPayload.code.status.ErrorStatus;
import com.snorlaexes.raem.global.apiPayload.exception.ExceptionHandler;
import com.snorlaexes.raem.global.config.jwt.TokenEntity;
import com.snorlaexes.raem.global.config.jwt.TokenRepository;
import com.snorlaexes.raem.global.smtp.SMTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final SMTPService smtpService;
    private final VerificationCodeRepository verificationCodeRepository;

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
    public void sendAuthenticationEmail(String userId, UserReqDTO req){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        int authenticationCode = generateAuthenticationCode();

        String subject = "[Ræm] 이메일 인증 코드 발송";
        String body = "<p>귀하의 이메일 주소를 통해 인증번호가 요청되었습니다. Pumble 인증 코드는 다음과 같습니다." +
                        "</p><br><strong>"+authenticationCode+"</strong><br><br><p>" +
                        "해당 코드는 10분 뒤 만료됩니다." +
                        "<br>Raem 운영팀</p>";
        smtpService.sendAsyncEmail(req.getNewEmail(), subject, body);

        VerificationCodeEntity newCode = VerificationCodeEntity.builder()
                .verifyCode(authenticationCode)
                .user(user)
                .build();
        verificationCodeRepository.save(newCode);
    }

    private Integer generateAuthenticationCode() {
        return (int) (Math.floor(Math.random() * (9999 - 1111 + 1)) + 1111);
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
    public void expireTest(String userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));
        int authenticationCode = generateAuthenticationCode();
        VerificationCodeEntity newCode = VerificationCodeEntity.builder()
                .verifyCode(authenticationCode)
                .user(user)
                .expiredAt(new Date(System.currentTimeMillis() + 10000))
                .build();
        verificationCodeRepository.save(newCode);
    }
}
