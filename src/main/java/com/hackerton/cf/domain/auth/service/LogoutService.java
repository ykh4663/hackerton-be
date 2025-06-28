package com.hackerton.cf.domain.auth.service;


import com.hackerton.cf.domain.auth.token.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final RefreshTokenService refreshTokenService;

    public void logout(Long userId) {
        refreshTokenService.deleteRefreshToken(userId);
    }
}