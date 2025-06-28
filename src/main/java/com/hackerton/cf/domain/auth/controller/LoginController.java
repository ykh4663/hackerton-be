package com.hackerton.cf.domain.auth.controller;


import com.hackerton.cf.domain.auth.dto.RefreshedTokens;
import com.hackerton.cf.domain.auth.dto.request.OauthLoginRequest;
import com.hackerton.cf.domain.auth.dto.request.RefreshTokenRequest;
import com.hackerton.cf.domain.auth.dto.request.TokenHealthCheckRequest;
import com.hackerton.cf.domain.auth.dto.response.OauthLoginResponse;
import com.hackerton.cf.domain.auth.dto.response.RefreshedTokensResponse;
import com.hackerton.cf.domain.auth.token.RefreshTokenService;
import com.hackerton.cf.domain.auth.token.TokenHealthCheckService;
import io.swagger.v3.oas.annotations.Operation;
import com.hackerton.cf.domain.auth.service.OauthService;
import com.hackerton.cf.global.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {
    private final OauthService oauthService;
    private final RefreshTokenService refreshTokenService;
    private final TokenHealthCheckService tokenHealthCheckService;

    /**
     * 개발중 소셜 로그인으로 사용자의 accessToken을 가져오기 어렵기 때문에 만든 임시 메서드
     */
    @PostMapping("/test/users/{userId}")
    @Operation(summary = "테스트 로그인", description = "테스트용 임시 토큰을 발급합니다.", tags = {"로그인"})
    public ResponseEntity<CommonResponse<OauthLoginResponse>> getToken(@PathVariable Long userId) {
        OauthLoginResponse response = oauthService.tmpTokenGet(userId);
        return ResponseEntity.ok(CommonResponse.createSuccess(response));
    }

    @PostMapping("/oauth")
    @Operation(summary = "Oauth 로그인", description = "Oauth 로그인을 하고 토큰들을 발급합니다.", tags = {"로그인"})
    public ResponseEntity<CommonResponse<OauthLoginResponse>> oauthLogin(@RequestBody OauthLoginRequest request) {
        OauthLoginResponse response = oauthService.login(request);
        return ResponseEntity.ok(CommonResponse.createSuccess(response));
    }
    //큰 차이는 없고 그냥 리프레시 토큰을 갱신한다는 의미에서 ed 붙인듯?
    @PatchMapping("/token/refresh")
    @Operation(summary = "토큰 리프레시", description = "accessToken과 refreshToken을 갱신합니다.", tags = {"로그인"})
    public ResponseEntity<CommonResponse<RefreshedTokensResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshedTokens refreshedTokens = refreshTokenService.getUserRefreshedTokens(
                request.getRefreshToken());
        RefreshedTokensResponse response = new RefreshedTokensResponse(
                refreshedTokens.accessToken(), refreshedTokens.refreshToken());
        return ResponseEntity.ok(CommonResponse.createSuccess(response));
    }
    //토큰이 만료됐는지(isExpired) 여부 체크
    @GetMapping("/token/health-check")
    @Operation(summary = "토큰 헬스체크", description = "토큰 헬스체크.", tags = {"로그인"})
    public ResponseEntity<CommonResponse<Void>> tokenHealthCheck(@ModelAttribute TokenHealthCheckRequest request) {
        tokenHealthCheckService.healthCheck(request.getToken());
        return ResponseEntity.ok(CommonResponse.createSuccessWithNoContent());
    }
}
