package com.hackerton.cf.domain.auth.service;


import com.hackerton.cf.domain.auth.OauthProvider;
import com.hackerton.cf.domain.auth.OauthProviderResolver;
import com.hackerton.cf.domain.auth.dto.RoleStatus;
import com.hackerton.cf.domain.auth.dto.request.OauthLoginRequest;
import com.hackerton.cf.domain.auth.dto.response.OauthLoginResponse;
import com.hackerton.cf.domain.auth.token.AuthToken;
import com.hackerton.cf.domain.auth.token.AuthTokenGenerator;
import com.hackerton.cf.domain.auth.token.RefreshTokenService;

import com.hackerton.cf.domain.user.dao.UserRepository;
import com.hackerton.cf.domain.user.domain.User;
import com.hackerton.cf.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OauthService {
    private final OauthProviderResolver oauthProviderResolver;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final AuthTokenGenerator authTokenGenerator;

    public OauthLoginResponse login(OauthLoginRequest request) {
        OauthProvider oauthProvider = oauthProviderResolver.find(request.getProviderName());
        String oauthId = request.getProviderName() + oauthProvider.getOAuthProviderUserId(request.getOauthCredential());

        //이미 가입된 유저인지 확인하고 가입되어 있지 않으면 회원가입 처리
        Optional<User> userOptional = userRepository.findByOauthId(oauthId);
        if (userOptional.isEmpty()) {
            User newUser = User.builder().oauthId(oauthId).role(RoleStatus.NONE.getStatus())
                    .build();
            User savedUser = userRepository.save(newUser);
            Long userId = savedUser.getId();
            AuthToken token = authTokenGenerator.generate(userId, savedUser.getOauthId());
            String accessToken = token.accessToken();
            String refreshToken = token.refreshToken();
            refreshTokenService.saveRefreshToken(userId, refreshToken);
            return new OauthLoginResponse(RoleStatus.NONE.getStatus(), accessToken, refreshToken);
        }

        //이미 가입한 유저인 경우 로그인 처리
        Long userId = userOptional.get().getId();
        final User user = userOptional.get();

        AuthToken token = authTokenGenerator.generate(userId, user.getOauthId());
        String accessToken = token.accessToken();
        String refreshToken = token.refreshToken();
        refreshTokenService.saveRefreshToken(userId, refreshToken);

        String role = user.getRole();

        // NONE: SMS 미인증
        if (RoleStatus.NONE.getStatus().equals(role)) {
            return new OauthLoginResponse(RoleStatus.NONE.getStatus(), accessToken, refreshToken);
        }

        // REGISTER: 프로필 등록 여부 확인
        if (RoleStatus.REGISTER.getStatus().equals(role)) {
            if (user.getProfile() == null) {
                return new OauthLoginResponse(RoleStatus.REGISTER.getStatus(), accessToken, refreshToken);
            } else {
                // ✅ 프로필이 등록되었으면 COMPLETE로 전환
                user.setRole(RoleStatus.COMPLETE.getStatus());
                userRepository.save(user);
                return new OauthLoginResponse(RoleStatus.COMPLETE.getStatus(), accessToken, refreshToken);
            }
        }

        // COMPLETE
        return new OauthLoginResponse(RoleStatus.COMPLETE.getStatus(), accessToken, refreshToken);
    }


    public OauthLoginResponse tmpTokenGet(Long userId) {
        User user = userService.getUserById(userId);

        AuthToken token = authTokenGenerator.generate(userId, user.getOauthId());
        String accessToken = token.accessToken();
        String refreshToken = token.refreshToken();
        refreshTokenService.saveRefreshToken(userId, refreshToken);

        return new OauthLoginResponse(RoleStatus.NONE.getStatus(), accessToken, refreshToken); // ✅ 정확한 Role 반환
    }

}

