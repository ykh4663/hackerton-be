package com.hackerton.cf.domain.auth.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getOauthId(String token) {
        String oauthId;
        try{
            oauthId = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("oauthId", String.class);
        }catch (Exception e){
            throw new RuntimeException();
        }
        return oauthId;
    }



    public Boolean isExpired(String token) {
        boolean before;
        try{
            before = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        }catch (Exception e){
            throw new RuntimeException();
        }
        return before;
    }




    public String createJwt(String category,Long userId,String oauthId, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("userId", userId)
                .claim("oauthId", oauthId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public Long getUserId(String token){
        Long userId;
        try{
            userId = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", Long.class);
        }catch (Exception e){
            throw new RuntimeException();
        }
        return userId;
    }

    public String getCategory(String token) {
        String category;
        try{
            category = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
        }catch (Exception e){
            throw new RuntimeException();
        }
        return category;
    }
}