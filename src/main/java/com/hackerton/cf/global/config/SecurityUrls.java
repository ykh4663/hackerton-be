package com.hackerton.cf.global.config;

import java.util.Arrays;
import java.util.List;

public class SecurityUrls {

    /** 인증을 생략할 URL 패턴(화이트리스트) */
    public static final List<String> AUTH_WHITELIST = Arrays.asList(
            // 로그인(네 컨트롤러가 /api/login/**)
            "/api/login/**",

            // Swagger / SpringDoc
            "/docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",

            // 기타
            "/error"
    );

    /** 허용된 CORS Origin (로컬 프론트) */
    public static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            "http://localhost:5173",        // Vite
            "http://127.0.0.1:5173",
            "http://localhost:8080",        // 로컬 리버스 프록시/백엔드
            "http://127.0.0.1:8080"
    );
}