package loginTemplate.kakao.domain.auth.kakao;



import loginTemplate.kakao.domain.auth.OauthClient;
import loginTemplate.kakao.domain.auth.OauthProvider;
import loginTemplate.kakao.domain.auth.domain.enums.OauthType;
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