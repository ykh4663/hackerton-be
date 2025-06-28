package com.hackerton.cf.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProfileErrorCode implements ErrorCode {
    NOT_FOUND_PROFILE(HttpStatus.NOT_FOUND, "프로필을 찾을 수 없습니다."),
    INVALID_CAREER_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 경력 코드입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}