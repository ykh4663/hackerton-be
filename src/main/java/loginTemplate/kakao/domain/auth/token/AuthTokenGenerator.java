package loginTemplate.kakao.domain.auth.token;


import loginTemplate.kakao.domain.auth.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthTokenGenerator {

    private final JwtUtil jwtUtil;

    @Value("${spring.jwt.accessToken.expiration}")
    private Long accessTokenExpiration;

    @Value("${spring.jwt.refreshToken.expiration}")
    private Long refreshTokenExpiration;


    public AuthToken generate(Long userId, String oauthId) {
        String accessToken = jwtUtil.createJwt("access_token", userId, oauthId,
                accessTokenExpiration);

        String refreshToken = jwtUtil.createJwt("refresh_token", userId, oauthId,
                refreshTokenExpiration);

        return new AuthToken(accessToken, refreshToken);
    }
}