package com.hackerton.cf.domain.auth.dto.response;

import com.hackerton.cf.domain.profile.dto.ProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OauthLoginResponse {
    private String role;
    private String accessToken;
    private String refreshToken;

}