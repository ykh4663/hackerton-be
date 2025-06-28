package com.hackerton.cf.domain.recommend.controller;

import com.hackerton.cf.domain.profile.dto.ProfileRequest;
import com.hackerton.cf.domain.profile.dto.ProfileResponse;
import com.hackerton.cf.domain.profile.service.ProfileService;

import com.hackerton.cf.domain.recommend.service.RecommendationService;
import com.hackerton.cf.domain.recommend.dto.AiCoverLetterRequest;
import com.hackerton.cf.global.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Recommend", description = "추천 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendationController {

    private final ProfileService profileService;
    private final RecommendationService recommendationService;

    /**
     * 추천 요청 (기본 프로필 + 수정값)
     */
    @Operation(
            summary     = "AI 기반 추천",
            description = "기본 프로필과 수정된 프로필을 조합하여 AI 추천 결과를 반환합니다."
    )
    @PostMapping
    public ResponseEntity<CommonResponse<Map<String, Object>>> recommendCustom(
            @AuthenticationPrincipal Long userId,
            @RequestBody ProfileRequest modifiedProfile
    ) {
        ProfileResponse base = profileService.getProfile(userId);
        ProfileResponse toSend = profileService.mergeProfile(base, modifiedProfile);

        Map<String, Object> result = recommendationService.getRecommendation(toSend);
        return ResponseEntity
                .ok(CommonResponse.createSuccess(result));
    }

    /**
     * 자기소개서 문장 수정 요청
     */
    @Operation(
            summary     = "자기소개서 수정 요청",
            description = "기업명과 자기소개서 원문을 받아 AI가 수정된 자기소개서를 반환합니다."
    )
    @PostMapping("/cover-letter")
    public ResponseEntity<CommonResponse<String>> reviseCoverLetter(
            @RequestBody AiCoverLetterRequest req
    ) {
        String modified = recommendationService.getModifiedCoverLetter(
                req.getCompany(), req.getContent()
        );
        return ResponseEntity
                .ok(CommonResponse.createSuccess(modified));
    }
}