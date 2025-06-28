package com.hackerton.cf.domain.auth.kakao;



import com.hackerton.cf.domain.auth.OauthClient;
import com.hackerton.cf.domain.auth.OauthProvider;
import com.hackerton.cf.domain.auth.domain.enums.OauthType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOauthProvider implements OauthProvider {
    private final KakaoOauthClient kakaoOauthClient;

    @Override
    public OauthType getOauthType() {
        return OauthType.KAKAO;
    }

    @Override
    public OauthClient getOAuthClient() {
        return kakaoOauthClient;
    }
}