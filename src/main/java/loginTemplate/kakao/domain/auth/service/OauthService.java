package loginTemplate.kakao.domain.auth.service;


import loginTemplate.kakao.domain.auth.OauthProvider;
import loginTemplate.kakao.domain.auth.OauthProviderResolver;
import loginTemplate.kakao.domain.auth.dto.request.OauthLoginRequest;
import loginTemplate.kakao.domain.auth.dto.response.OauthLoginResponse;
import loginTemplate.kakao.domain.auth.token.AuthToken;
import loginTemplate.kakao.domain.auth.token.AuthTokenGenerator;
import loginTemplate.kakao.domain.auth.token.RefreshTokenService;
import loginTemplate.kakao.domain.user.dao.UserRepository;
import loginTemplate.kakao.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OauthService {
    private final OauthProviderResolver oauthProviderResolver;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final AuthTokenGenerator authTokenGenerator;

    public OauthLoginResponse login(OauthLoginRequest request) {
        OauthProvider oauthProvider = oauthProviderResolver.find(request.getProviderName());
        String oauthId = request.getProviderName() + oauthProvider.getOAuthProviderUserId(request.getOauthCredential());

        //이미 가입된 유저인지 확인하고 가입되어 있지 않으면 회원가입 처리
        Optional<User> userOptional = userRepository.findByOauthId(oauthId);
        if (userOptional.isEmpty()) {
            User newUser = User.builder().oauthId(oauthId).build();
            User savedUser = userRepository.save(newUser);
            Long userId = savedUser.getId();
            AuthToken token = authTokenGenerator.generate(userId, savedUser.getOauthId());
            String accessToken = token.accessToken();
            String refreshToken = token.refreshToken();
            refreshTokenService.saveRefreshToken(userId, refreshToken);
            return new OauthLoginResponse(accessToken, refreshToken);
        }

        //이미 가입한 유저인 경우 로그인 처리
        Long userId = userOptional.get().getId();
        final User user = userOptional.get();
        AuthToken token = authTokenGenerator.generate(userId, user.getOauthId());
        String accessToken = token.accessToken();
        String refreshToken = token.refreshToken();
        refreshTokenService.saveRefreshToken(userId, refreshToken);

        // 가입과 프로필 등록까지 완료된 유저
        return new OauthLoginResponse(accessToken, refreshToken);
    }



    public OauthLoginResponse tmpTokenGet(Long userId) {
        //이미 가입된 유저인지 확인하고 가입되어 있지 않으면 회원가입 처리
        Optional<User> userOptional = userRepository.findById(userId);

        User user = userOptional.get();

        AuthToken token = authTokenGenerator.generate(userId, user.getOauthId());
        String accessToken = token.accessToken();
        String refreshToken = token.refreshToken();
        refreshTokenService.saveRefreshToken(userId, refreshToken);

        return new OauthLoginResponse(accessToken, refreshToken);
    }
}
