package com.snorlaexes.raem.domain.auth;

import com.snorlaexes.raem.global.apiPayload.code.status.ErrorStatus;
import com.snorlaexes.raem.global.apiPayload.exception.ExceptionHandler;
import com.snorlaexes.raem.domain.user.UserEntity;
import com.snorlaexes.raem.domain.user.UserRepository;
import com.snorlaexes.raem.global.config.jwt.JwtTokenUtil;
import com.snorlaexes.raem.global.config.jwt.TokenEntity;
import com.snorlaexes.raem.global.config.jwt.TokenRepository;
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
    private final TokenRepository tokenRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Transactional
    public UserEntity createUser(AuthReqDTO.SignUpReqDTO req) {
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
                .imageUrl(null)
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
        return userRepository.save(newUser);
    }

    @Transactional
    public TokenEntity.Tokens signIn(AuthReqDTO.SignInReqDTO req) {
        String refreshToken = null;

        UserEntity user = userRepository.findByEmail(req.email);

        //존재하는 계정인지 확인
        if (user == null) {
            throw new ExceptionHandler(ErrorStatus._AUTHENTICATION_FAILED);
        }

        //비밀번호 비교
        if (!passwordEncoder.matches(req.password, user.getPassword())) {
            throw new ExceptionHandler(ErrorStatus._AUTHENTICATION_FAILED);
        }

        //최초 로그인일 경우 rtk 발급
        if (tokenRepository.findByUserId(user.getId()) == null) {
            refreshToken = jwtTokenUtil.generateRefreshToken();

            TokenEntity.RefreshToken newRefreshToken = TokenEntity.RefreshToken.builder()
                    .refreshToken(refreshToken)
                    .user(user)
                    .build();

            tokenRepository.save(newRefreshToken);
        }

        String accessToken = jwtTokenUtil.generateAccessToken(user.getId());

        return TokenEntity.Tokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
