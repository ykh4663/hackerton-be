package com.hackerton.cf.domain.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Schema(title = "ProfileRequest", description = "프로필 생성/수정 요청 DTO")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class ProfileRequest {
    @Schema(description = "변경할 닉네임", example = "johndoe", nullable = true)
    private String nickname;

    @Schema(description = "기본 프로필 정보", implementation = ProfileDto.class)
    @NotNull(message = "basic 정보는 필수입니다.")
    private ProfileDto basic;

    @Schema(description = "경력 코드 (0=신입, 1=경력)", example = "0")
    @NotNull(message = "careerCode는 필수입니다.")
    private Integer careerCode;

    @Schema(description = "능력 코드 리스트 (0~10)", example = "[1,5,7]")
    @NotNull(message = "abilities는 필수입니다.")
    @Size(min = 1, message = "최소 하나의 ability를 선택해야 합니다.")
    private List<Integer> abilities;




}