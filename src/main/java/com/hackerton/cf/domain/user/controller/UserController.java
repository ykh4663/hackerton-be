package com.hackerton.cf.domain.user.controller;


import com.hackerton.cf.global.docs.ErrorExamples;
import com.hackerton.cf.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import com.hackerton.cf.domain.user.service.UserService;
import com.hackerton.cf.global.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴합니다.", tags = {"사용자"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패/토큰 만료",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name="UNAUTHORIZED", value = ErrorExamples.UNAUTHORIZED),
                                    @ExampleObject(name="EXPIRED_ACCESS_TOKEN", value = ErrorExamples.EXPIRED_ACCESS_TOKEN)
                            })),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="FORBIDDEN", value = ErrorExamples.FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="NOTFOUND_USER", value = ErrorExamples.USER_NOT_FOUND))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name="INTERNAL_SERVER_ERROR", value = ErrorExamples.INTERNAL_SERVER_ERROR)))
    })
    public ResponseEntity<CommonResponse<Void>> deleteUser(@AuthenticationPrincipal Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(CommonResponse.createSuccessWithNoContent());
    }
}
