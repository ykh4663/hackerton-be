package com.hackerton.cf.domain.auth.controller;


import com.hackerton.cf.domain.auth.dto.RefreshedTokens;
import com.hackerton.cf.domain.auth.dto.request.OauthLoginRequest;
import com.hackerton.cf.domain.auth.dto.request.RefreshTokenRequest;
import com.hackerton.cf.domain.auth.dto.request.TokenHealthCheckRequest;
import com.hackerton.cf.domain.auth.dto.response.OauthLoginResponse;
import com.hackerton.cf.domain.auth.dto.response.RefreshedTokensResponse;
import com.hackerton.cf.domain.auth.token.RefreshTokenService;
import com.hackerton.cf.domain.auth.token.TokenHealthCheckService;
import com.hackerton.cf.global.docs.ErrorExamples;
import com.hackerton.cf.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import com.hackerton.cf.domain.auth.service.OauthService;
import com.hackerton.cf.global.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @PostMapping("/test/users/{userId}")
    @Operation(summary = "테스트 로그인", description = "테스트용 임시 토큰을 발급합니다.", tags = {"로그인"})
    public ResponseEntity<CommonResponse<OauthLoginResponse>> getToken(@PathVariable Long userId) {
        OauthLoginResponse response = oauthService.tmpTokenGet(userId);
        return ResponseEntity.ok(CommonResponse.createSuccess(response));
    }

    @PostMapping("/oauth")
    @Operation(summary = "Oauth 로그인", description = "Oauth 로그인을 하고 토큰들을 발급합니다.", tags = {"로그인"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400", description = "유효성 실패(INVALID_PARAMETER) 등",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="INVALID_PARAMETER", value= ErrorExamples.INVALID_PARAMETER))),
            @ApiResponse(responseCode = "401", description = "ID 토큰 없음/유효하지 않음 등",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name="UNAUTHORIZED", value=ErrorExamples.UNAUTHORIZED)
                            })),
            @ApiResponse(responseCode = "403", description = "OAuth 오류/접근 거부",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name="ACCESS_DENIED", value=ErrorExamples.ACCESS_DENIED)
                            })),
            @ApiResponse(responseCode = "404", description = "OAuth ID 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="OAUTH_ID_NOT_FOUND", value=ErrorExamples.OAUTH_ID_NOT_FOUND))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="INTERNAL_SERVER_ERROR", value=ErrorExamples.INTERNAL_SERVER_ERROR)))
    })
    public ResponseEntity<CommonResponse<OauthLoginResponse>> oauthLogin(@RequestBody OauthLoginRequest request) {
        OauthLoginResponse response = oauthService.login(request);
        return ResponseEntity.ok(CommonResponse.createSuccess(response));
    }

    @PatchMapping("/token/refresh")
    @Operation(summary = "토큰 리프레시", description = "accessToken과 refreshToken을 갱신합니다.", tags = {"로그인"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400", description = "리프레시 토큰 누락 등",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name="MISSING_REFRESH_TOKEN", value=ErrorExamples.MISSING_REFRESH_TOKEN),
                                    @ExampleObject(name="INVALID_PARAMETER", value=ErrorExamples.INVALID_PARAMETER)
                            })),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰 만료/불일치",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name="EXPIRED_REFRESH_TOKEN", value=ErrorExamples.EXPIRED_REFRESH_TOKEN),
                                    @ExampleObject(name="INVALID_REFRESH_TOKEN", value=ErrorExamples.INVALID_REFRESH_TOKEN)
                            })),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="INTERNAL_SERVER_ERROR", value=ErrorExamples.INTERNAL_SERVER_ERROR)))
    })
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유효한 토큰",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400", description = "파라미터 누락/형식 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="INVALID_PARAMETER", value=ErrorExamples.INVALID_PARAMETER))),
            @ApiResponse(responseCode = "401", description = "토큰 만료",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="EXPIRED_TOKEN", value=ErrorExamples.EXPIRED_TOKEN))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="INTERNAL_SERVER_ERROR", value=ErrorExamples.INTERNAL_SERVER_ERROR)))
    })
    public ResponseEntity<CommonResponse<Void>> tokenHealthCheck(@ModelAttribute TokenHealthCheckRequest request) {
        tokenHealthCheckService.healthCheck(request.getToken());
        return ResponseEntity.ok(CommonResponse.createSuccessWithNoContent());
    }
}
