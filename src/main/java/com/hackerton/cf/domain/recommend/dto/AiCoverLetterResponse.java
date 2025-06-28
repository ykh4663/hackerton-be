package com.hackerton.cf.domain.recommend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Schema(title = "AiCoverLetterResponse", description = "자기소개서 수정 응답 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiCoverLetterResponse {
    @Schema(description = "AI가 수정한 자기소개서 내용", example = "저는 소프트웨어 엔지니어링에 열정을 가지고 있습니다.")
    private String modifiedContent;
}