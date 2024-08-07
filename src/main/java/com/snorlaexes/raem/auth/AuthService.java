package com.snorlaexes.raem.auth;

import com.snorlaexes.raem.global.apiPayload.code.status.ErrorStatus;
import com.snorlaexes.raem.global.apiPayload.exception.ExceptionHandler;
import com.snorlaexes.raem.user.UserEntity;
import com.snorlaexes.raem.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity createUser(AuthRequest.SignUpReqDTO req) {
        //이메일 중복 확인
        if (userRepository.findByEmail(req.email) != null) {
            throw new ExceptionHandler(ErrorStatus._USER_ALREADY_EXIST);
        }

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(req.password);

        UserEntity newUser = UserEntity.builder()
                .username(req.username)
                .email(req.email)
                .password(encodedPassword)
                .provider("local")
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
        return userRepository.save(newUser);
    }
}
