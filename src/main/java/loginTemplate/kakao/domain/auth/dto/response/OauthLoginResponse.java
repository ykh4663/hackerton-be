package loginTemplate.kakao.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OauthLoginResponse {
    private String accessToken;
    private String refreshToken;
}