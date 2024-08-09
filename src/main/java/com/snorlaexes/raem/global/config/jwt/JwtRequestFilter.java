package com.snorlaexes.raem.global.config.jwt;

import com.snorlaexes.raem.global.apiPayload.code.status.ErrorStatus;
import com.snorlaexes.raem.global.apiPayload.exception.ExceptionHandler;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetails jwtUserDetails;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");

        String userId;
        String jwtToken;

        try {
            logger.info("Filter Chain in " + request.getRequestURI());
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                try {
                    userId = jwtTokenUtil.extractUserId(jwtToken);
                } catch (IllegalArgumentException e) {
                    System.out.println("Unable to get JWT Token");
                    throw new ExceptionHandler(ErrorStatus._UNAUTHORIZED);
                } catch (ExpiredJwtException e) {
                    System.out.println("JWT Token has expired");
                    throw new ExceptionHandler(ErrorStatus._UNAUTHORIZED);
                }
            } else {
                logger.warn("JWT Token does not begin with Bearer String");
                throw new ExceptionHandler(ErrorStatus._UNAUTHORIZED);
            }

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = this.jwtUserDetails.loadUserByUsername(userId);

                if (jwtTokenUtil.validateAccessToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, null);
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error in JwtRequestFilter: ", e);
            throw e;
        }


    }
}
