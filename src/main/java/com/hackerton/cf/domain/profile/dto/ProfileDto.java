package com.hackerton.cf.domain.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(title = "ProfileBasicDto", description = "프로필의 기본 정보 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {
    @Schema(description = "학년", example = "0:4학년, 1:2,3학년")
    private Integer universityYear;
    @Schema(description = "전공명", example = "컴퓨터공학과")
    private String department;
    @Schema(description = "자격증 정보 (콤마 구분)", example = "정보처리기사")
    private String certifications;
    @Schema(description = "영어 성적", example = "TOEIC 900")
    private String languages;
    @Schema(description = "수상 이력 (콤마 구분)", example = "장학금 수여,프로젝트 우수상")
    private String awards;
    @Schema(description = "경력 및 경험 요약", example = "Java 백엔드 개발 3년")
    private String experienceText;
}