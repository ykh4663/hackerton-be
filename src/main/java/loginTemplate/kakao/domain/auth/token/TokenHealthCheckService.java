package loginTemplate.kakao.domain.auth.token;

import loginTemplate.kakao.domain.auth.jwt.JwtUtil;
import loginTemplate.kakao.global.error.ApplicationException;
import loginTemplate.kakao.global.error.SecurityErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenHealthCheckService {
    private final JwtUtil jwtUtil;

    public void healthCheck(String token) {
        try {
            jwtUtil.isExpired(token);
        } catch (Exception e) {
            throw new ApplicationException(SecurityErrorCode.EXPIRED_TOKEN);
        }
    }
}
