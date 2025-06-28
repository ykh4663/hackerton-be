package com.hackerton.cf.domain.auth.kakao;

import com.hackerton.cf.domain.auth.OauthClient;
import com.hackerton.cf.domain.auth.domain.enums.OauthType;
import com.hackerton.cf.domain.auth.kakao.KakaoOauthClient;
import com.hackerton.cf.domain.auth.kakao.KakaoOauthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KakaoOauthProviderTest {
    @Mock
    private KakaoOauthClient kakaoOauthClient;
    @InjectMocks
    private KakaoOauthProvider kakaoOauthProvider;

    @Test
    @DisplayName("카카오 Client 를 반환한다")
    void testGetOAuthClient() {
        OauthClient oauthClient = kakaoOauthProvider.getOAuthClient();
        assertThat(oauthClient).isNotNull();
        assertThat(oauthClient).isEqualTo(kakaoOauthClient);
    }

    @Test
    @DisplayName("getOauthType 메서드가 OauthType.KAKAO를 반환한다")
    void testGetOauthType() {
        assertThat(kakaoOauthProvider.getOauthType()).isEqualTo(OauthType.KAKAO);
    }
}