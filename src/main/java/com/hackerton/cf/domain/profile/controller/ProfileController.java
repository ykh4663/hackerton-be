package com.hackerton.cf.domain.profile.controller;

import com.hackerton.cf.domain.profile.dto.ProfileRequest;
import com.hackerton.cf.domain.profile.dto.ProfileResponse;
import com.hackerton.cf.domain.profile.service.ProfileService;
import com.hackerton.cf.global.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @GetMapping("/me")
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
    @PutMapping("/me")
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