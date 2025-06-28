package com.hackerton.cf.domain.auth;


import com.hackerton.cf.domain.auth.domain.enums.OauthType;

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