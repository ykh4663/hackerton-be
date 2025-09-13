package com.hackerton.cf.domain.recommend.controller;

import com.hackerton.cf.domain.profile.dto.ProfileRequest;
import com.hackerton.cf.domain.profile.dto.ProfileResponse;
import com.hackerton.cf.domain.profile.service.ProfileService;

import com.hackerton.cf.domain.recommend.service.RecommendationService;
import com.hackerton.cf.domain.recommend.dto.AiCoverLetterRequest;
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
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="INVALID_PARAMETER", value = ErrorExamples.INVALID_PARAMETER))),
            @ApiResponse(responseCode = "401", description = "인증 실패/토큰 만료",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name="UNAUTHORIZED", value = ErrorExamples.UNAUTHORIZED),
                                    @ExampleObject(name="EXPIRED_ACCESS_TOKEN", value = ErrorExamples.EXPIRED_ACCESS_TOKEN)
                            })),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="FORBIDDEN", value = ErrorExamples.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = "프로필 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="NOT_FOUND_PROFILE", value = ErrorExamples.PROFILE_NOT_FOUND))),
            @ApiResponse(responseCode = "500", description = "AI 호출 실패/서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name="AI_CALL_FAILURE", value = ErrorExamples.AI_CALL_FAILURE),
                                    @ExampleObject(name="INTERNAL_SERVER_ERROR", value = ErrorExamples.INTERNAL_SERVER_ERROR)
                            }))
    })
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="INVALID_PARAMETER", value = """
                  {
                    "code":"INVALID_PARAMETER",
                    "message":"유효하지 않은 요청 파라미터가 포함되어 있습니다.",
                    "errors":[
                      {"field":"company","rejectedValue":null,"reason":"must not be blank"},
                      {"field":"content","rejectedValue":null,"reason":"must not be blank"}
                    ]
                  }"""))),
            @ApiResponse(responseCode = "500", description = "AI 호출 실패/서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name="AI_CALL_FAILURE", value = ErrorExamples.AI_CALL_FAILURE),
                                    @ExampleObject(name="INTERNAL_SERVER_ERROR", value = ErrorExamples.INTERNAL_SERVER_ERROR)
                            }))
    })
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