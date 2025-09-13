package com.hackerton.cf.domain.profile.controller;

import com.hackerton.cf.domain.profile.dto.ProfileRequest;
import com.hackerton.cf.domain.profile.dto.ProfileResponse;
import com.hackerton.cf.domain.profile.service.ProfileService;
import com.hackerton.cf.global.common.dto.CommonResponse;
import com.hackerton.cf.global.docs.ErrorExamples;
import com.hackerton.cf.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@Tag(name = "Profile", description = "프로필 API")
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 본인 프로필 조회
     */
    @Operation(
            summary     = "본인 프로필 조회",
            description = "인증된 사용자의 프로필 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패/토큰 만료",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "UNAUTHORIZED",
                                            value = "{ \"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\" }"),
                                    @ExampleObject(name = "EXPIRED_ACCESS_TOKEN",
                                            value = "{ \"code\":\"EXPIRED_ACCESS_TOKEN\",\"message\":\"액세스 토큰이 만료되었습니다.\" }")
                            })),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "FORBIDDEN",
                                    value = "{ \"code\":\"FORBIDDEN\",\"message\":\"권한이 없습니다.\" }"))),
            @ApiResponse(responseCode = "404", description = "프로필 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "NOT_FOUND_PROFILE", value = ErrorExamples.PROFILE_NOT_FOUND))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="INTERNAL_SERVER_ERROR",
                                    value = "{ \"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"서버 오류가 발생했습니다.\" }")))
    })
    @GetMapping
    public ResponseEntity<CommonResponse<ProfileResponse>> getMyProfile(
            @AuthenticationPrincipal Long userId
    ) {
        ProfileResponse resp = profileService.getProfile(userId);
        return ResponseEntity
                .ok(CommonResponse.createSuccess(resp));
    }

    /**
     * 프로필 생성/수정
     */
    @Operation(
            summary     = "프로필 생성/수정",
            description = "인증된 사용자의 프로필을 생성하거나 업데이트합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성/수정 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류/경력코드 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "INVALID_PARAMETER", value = ErrorExamples.PROFILE_INVALID_PARAMETER),
                                    @ExampleObject(name = "INVALID_CAREER_CODE", value = ErrorExamples.INVALID_CAREER_CODE)
                            })),
            @ApiResponse(responseCode = "401", description = "인증 실패/토큰 만료",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "UNAUTHORIZED",
                                            value = "{ \"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\" }"),
                                    @ExampleObject(name = "EXPIRED_ACCESS_TOKEN",
                                            value = "{ \"code\":\"EXPIRED_ACCESS_TOKEN\",\"message\":\"액세스 토큰이 만료되었습니다.\" }")
                            })),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "FORBIDDEN",
                                    value = "{ \"code\":\"FORBIDDEN\",\"message\":\"권한이 없습니다.\" }"))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "NOTFOUND_USER",
                                    value = "{ \"code\":\"NOTFOUND_USER\",\"message\":\"해당 사용자를 찾을 수 없습니다.\" }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="INTERNAL_SERVER_ERROR",
                                    value = "{ \"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"서버 오류가 발생했습니다.\" }")))
    })
    @PutMapping
    public ResponseEntity<CommonResponse<Void>> upsertProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid ProfileRequest req
    ) {
        profileService.upsertProfile(userId, req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.createSuccessWithNoContent("프로필 업데이트가 완료되었습니다."));
    }

}