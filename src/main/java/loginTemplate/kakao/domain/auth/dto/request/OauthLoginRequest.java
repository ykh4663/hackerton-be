package loginTemplate.kakao.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OauthLoginRequest {
    private String providerName;
    private String oauthCredential;
}