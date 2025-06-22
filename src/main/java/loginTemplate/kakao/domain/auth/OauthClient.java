package loginTemplate.kakao.domain.auth;

public interface OauthClient {
    String getOAuthProviderUserId(String accessToken);
}