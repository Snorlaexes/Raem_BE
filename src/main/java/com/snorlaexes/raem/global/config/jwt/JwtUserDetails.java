package com.snorlaexes.raem.global.config.jwt;

import com.snorlaexes.raem.global.apiPayload.code.status.ErrorStatus;
import com.snorlaexes.raem.global.apiPayload.exception.ExceptionHandler;
import com.snorlaexes.raem.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetails implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus._USER_NOT_FOUND));

        return User.builder()
                .username(userId)
                .build();
    }
}
