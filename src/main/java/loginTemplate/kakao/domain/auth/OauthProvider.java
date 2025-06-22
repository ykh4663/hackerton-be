package loginTemplate.kakao.domain.auth;


import loginTemplate.kakao.domain.auth.domain.enums.OauthType;

public interface OauthProvider {
    OauthType getOauthType();
    OauthClient getOAuthClient();

    default boolean match(String providerName){
        return providerName.equals(getOauthType().getTypeName());
    }

    default String getOAuthProviderUserId(String accessToken) {
        OauthClient client = getOAuthClient();
        return client.getOAuthProviderUserId(accessToken);
    }

}