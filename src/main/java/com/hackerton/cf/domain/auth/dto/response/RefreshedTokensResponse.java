package com.hackerton.cf.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshedTokensResponse {
    private String accessToken;
    private String refreshToken;
}