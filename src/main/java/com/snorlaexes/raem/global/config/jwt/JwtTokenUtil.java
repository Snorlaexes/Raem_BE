package com.snorlaexes.raem.global.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    private SecretKey getSigningKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return (Claims) Jwts.parserBuilder().setSigningKey(this.getSigningKey()).build().parseClaimsJws(token);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateAccessToken(String subject) {
        return Jwts.builder().setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 3))
                .signWith(this.getSigningKey()).compact();
    }

    public String generateRefreshToken() {
        return Jwts.builder().setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365))
                .signWith(this.getSigningKey()).compact();
    }

    public Boolean validateAccessToken(String token, UserDetails userDetails) {
        final String userId = extractUserId(token);
        return (userId.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Boolean validateRefreshToken(String token) {
        return !isTokenExpired(token);
    }
}
