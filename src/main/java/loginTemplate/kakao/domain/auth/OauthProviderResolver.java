package loginTemplate.kakao.domain.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OauthProviderResolver {
    private final List<OauthProvider> oauthProviders;
    public OauthProvider find(String providerName){
        return oauthProviders.stream()
                .filter(provider -> provider.match(providerName))
                .findAny()
                .orElseThrow(RuntimeException::new);
    }
}
