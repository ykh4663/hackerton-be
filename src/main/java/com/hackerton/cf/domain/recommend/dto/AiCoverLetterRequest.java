package com.hackerton.cf.domain.recommend.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(title = "AiCoverLetterRequest", description = "자기소개서 수정 요청 DTO")
@Getter
@Setter
@NoArgsConstructor
public class AiCoverLetterRequest {
    @Schema(description = "회사명", example = "삼성전자")
    private String company;
    @Schema(description = "원본 자기소개서 내용", example = "저는 개발을 좋아합니다.")
    private String content;
}