package com.hackerton.cf.domain.profile.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Schema(title = "ProfileResponse", description = "프로필 조회 응답 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    @Schema(description = "기본 프로필 정보", implementation = ProfileBasicDto.class)
    private ProfileBasicDto basic;
    @Schema(description = "선택된 능력 코드 리스트", example = "[1,5,7]")
    private List<Integer> abilities;
    @Schema(description = "경력 상태 (\"신입\" 또는 \"경력\")", example = "신입")
    private String careerCode;




}
