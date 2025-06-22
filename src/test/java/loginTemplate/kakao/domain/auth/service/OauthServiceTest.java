package loginTemplate.kakao.domain.auth.service;

import loginTemplate.kakao.domain.auth.OauthProvider;
import loginTemplate.kakao.domain.auth.OauthProviderResolver;
import loginTemplate.kakao.domain.auth.dto.request.OauthLoginRequest;
import loginTemplate.kakao.domain.auth.dto.response.OauthLoginResponse;
import loginTemplate.kakao.domain.auth.jwt.JwtUtil;
import loginTemplate.kakao.domain.auth.token.AuthToken;
import loginTemplate.kakao.domain.auth.token.AuthTokenGenerator;
import loginTemplate.kakao.domain.auth.token.RefreshTokenService;
import loginTemplate.kakao.domain.user.dao.UserRepository;
import loginTemplate.kakao.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OauthServiceTest {

    @InjectMocks
    private OauthService oauthService;
    @Mock
    private OauthProviderResolver oauthProviderResolver;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OauthProvider oauthProvider;
    @Mock
    private AuthTokenGenerator authTokenGenerator;
    @Mock
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("처음 로그인에 성공하여 회원가입을 한 뒤 액세스,리프레시 토큰을 반환한다")
    void testFirstLogin_Success() {
        //given
        String providerName = "provider";
        String token = "oauth_token";
        OauthLoginRequest request = new OauthLoginRequest(providerName, token);
        String oauthId = providerName + "123456";

        when(oauthProviderResolver.find(providerName)).thenReturn(oauthProvider);
        when(oauthProvider.getOAuthProviderUserId(token)).thenReturn("123456");
        when(userRepository.findByOauthId(oauthId)).thenReturn(Optional.empty());
        User user = User.builder().oauthId(oauthId).build();
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(authTokenGenerator.generate(1L, oauthId)).thenReturn(
                new AuthToken("access_token", "refresh_token"));

        // When
        OauthLoginResponse response = oauthService.login(request);

        // Then
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
    }

    @Test
    @DisplayName("존재하지 않는 Provider로 로그인을 요청하여 로그인에 실패한다.")
    void testLogin_Failure() {
        // Given
        String providerName = "nonexistent";
        String token = "oauth_token";
        OauthLoginRequest request = new OauthLoginRequest(providerName, token);

        // Mocking
        when(oauthProviderResolver.find(providerName)).thenThrow(new RuntimeException());

        // When & Then
        assertThrows(RuntimeException.class, () -> oauthService.login(request));
    }

    @Test
    @DisplayName("회원가입이 된 상태에서 로그인에 성공하여 액세스, 리프레시 토큰을 반환한다.")
    void testNotFirstLogin_Success() {
        //given
        String providerName = "provider";
        String token = "oauth_token";
        OauthLoginRequest request = new OauthLoginRequest(providerName, token);
        String oauthId = providerName + "123456";

        when(oauthProviderResolver.find(providerName)).thenReturn(oauthProvider);
        when(oauthProvider.getOAuthProviderUserId(token)).thenReturn("123456");
        User existingUser = User.builder().oauthId(oauthId).build();
        ReflectionTestUtils.setField(existingUser, "id", 1L);

        when(userRepository.findByOauthId(oauthId)).thenReturn(Optional.of(existingUser));
        when(authTokenGenerator.generate(1L, oauthId)).thenReturn(
                new AuthToken("access_token", "refresh_token"));

        // When
        OauthLoginResponse response = oauthService.login(request);

        // Then
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
    }
}