package loginTemplate.kakao.domain.auth.controller;


import loginTemplate.kakao.domain.auth.service.LogoutService;
import loginTemplate.kakao.global.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logout")
public class LogoutController {


    private final LogoutService logoutService;

    @PatchMapping
    public ResponseEntity<CommonResponse<Void>> logout(@AuthenticationPrincipal Long userId) {
        logoutService.logout(userId);
        return ResponseEntity.ok(CommonResponse.createSuccessWithNoContent());
    }
}
