package com.hackerton.cf.domain.auth.controller;


import com.hackerton.cf.domain.auth.service.LogoutService;
import com.hackerton.cf.global.common.dto.CommonResponse;
import com.hackerton.cf.global.docs.ErrorExamples;
import com.hackerton.cf.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logout")
public class LogoutController {


    private final LogoutService logoutService;

    @PatchMapping
    @Operation(summary = "로그아웃", description = "리프레시 토큰 제거")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Authorization 헤더 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="INVALID_AUTHORIZATION_HEADER", value=ErrorExamples.INVALID_AUTH_HEADER))),
            @ApiResponse(responseCode = "401", description = "인증 실패/토큰 만료/무효",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name="UNAUTHORIZED", value= ErrorExamples.UNAUTHORIZED),
                                    @ExampleObject(name="INVALID_ACCESS_TOKEN", value=ErrorExamples.INVALID_ACCESS_TOKEN),
                                    @ExampleObject(name="EXPIRED_ACCESS_TOKEN", value=ErrorExamples.EXPIRED_ACCESS_TOKEN)
                            })),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="ACCESS_DENIED", value=ErrorExamples.ACCESS_DENIED))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="NOTFOUND_USER", value=ErrorExamples.USER_NOT_FOUND))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="INTERNAL_SERVER_ERROR", value=ErrorExamples.INTERNAL_SERVER_ERROR)))
    })
    public ResponseEntity<CommonResponse<Void>> logout(@AuthenticationPrincipal Long userId) {
        logoutService.logout(userId);
        return ResponseEntity.ok(CommonResponse.createSuccessWithNoContent());
    }
}
