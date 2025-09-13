package com.hackerton.cf.global.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
@Schema(name = "ErrorResponse", description = "에러 응답 형식")
public class ErrorResponse {

    @Schema(description = "에러 코드(ENUM 이름)", example = "INVALID_REFRESH_TOKEN")
    private final String code;
    @Schema(description = "에러 메시지", example = "유효하지 않은 리프레시 토큰입니다.")
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "필드 유효성 에러 목록(@Valid 실패 시)", nullable = true)
    private final List<ValidationError> errors;

    @Getter
    @Builder
    @RequiredArgsConstructor
    @Schema(name = "ValidationError", description = "유효성 검증 실패 정보")
    public static class ValidationError {
        @Schema(description = "필드명", example = "refreshToken")
        private final String field;
        @Schema(description = "메시지", example = "비어 있으면 안됩니다")
        private final String message;

        public static ValidationError of(final FieldError fieldError) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }
    }
}