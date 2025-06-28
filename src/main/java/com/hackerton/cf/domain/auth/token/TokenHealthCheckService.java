package com.hackerton.cf.domain.auth.token;

import com.hackerton.cf.domain.auth.jwt.JwtUtil;
import com.hackerton.cf.global.error.ApplicationException;
import com.hackerton.cf.global.error.SecurityErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenHealthCheckService {
    private final JwtUtil jwtUtil;

    public void healthCheck(String token) {
        try {
            jwtUtil.isExpired(token);
        } catch (Exception e) {
            throw new ApplicationException(SecurityErrorCode.EXPIRED_TOKEN);
        }
    }
}
