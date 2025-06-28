package com.hackerton.cf.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AiException implements ErrorCode {
    AI_CALL_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR,
            "AI 추천 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    ;

    private final HttpStatus httpStatus;
    private final String message;
}