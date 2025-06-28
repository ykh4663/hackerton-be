package com.hackerton.cf.domain.auth;

public interface OauthClient {
    String getOAuthProviderUserId(String accessToken);
}