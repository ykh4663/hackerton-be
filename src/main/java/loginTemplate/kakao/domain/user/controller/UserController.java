package loginTemplate.kakao.domain.user.controller;


import io.swagger.v3.oas.annotations.Operation;
import loginTemplate.kakao.domain.user.service.UserService;
import loginTemplate.kakao.global.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<CommonResponse<Void>> deleteUser(@AuthenticationPrincipal Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(CommonResponse.createSuccessWithNoContent());
    }
}
