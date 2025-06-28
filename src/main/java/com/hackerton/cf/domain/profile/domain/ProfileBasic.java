package com.hackerton.cf.domain.profile.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileBasic {

    /** 0 → 4학년, 1 → 2~3학년 */
    @Column(name = "university_year", nullable = false)
    private Integer universityYear;



    @Column(name = "department", nullable = false)
    private String department;

    /** 전공 자격증 (콤마로 구분된 문자열) */
    @Column(name = "certifications", length = 500)
    private String certifications;

    /** 어학 (콤마로 구분된 문자열) */
    @Column(name = "languages", length = 500)
    private String languages;

    /** 수상이력 (콤마로 구분된 문자열 또는 자유 텍스트) */
    @Column(name = "awards", length = 1000)
    private String awards;

    @Column(columnDefinition = "TEXT")
    private String experienceText; // 사용자가 자유롭게 서술
}