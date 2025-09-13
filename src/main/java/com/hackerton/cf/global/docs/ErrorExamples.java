package com.hackerton.cf.global.docs;

public final class ErrorExamples {
    private ErrorExamples() {}

    // 400
    public static final String INVALID_PARAMETER = """
        { "code": "INVALID_PARAMETER", "message": "유효하지 않은 요청 파라미터가 포함되어 있습니다.", "errors": [
          {"field":"refreshToken","rejectedValue":"","reason":"must not be blank"}
        ]}""";

    public static final String MISSING_REFRESH_TOKEN = """
        { "code": "MISSING_REFRESH_TOKEN", "message": "리프레시 토큰이 없습니다." }""";

    public static final String INVALID_AUTH_HEADER = """
        { "code": "INVALID_AUTHORIZATION_HEADER", "message": "잘못된 Authorization 헤더입니다." }""";

    // 401
    public static final String UNAUTHORIZED = """
        { "code": "UNAUTHORIZED", "message": "인증이 필요합니다." }""";

    public static final String INVALID_ACCESS_TOKEN = """
        { "code": "INVALID_ACCESS_TOKEN", "message": "유효하지 않은 액세스 토큰입니다." }""";

    public static final String EXPIRED_ACCESS_TOKEN = """
        { "code": "EXPIRED_ACCESS_TOKEN", "message": "액세스 토큰이 만료되었습니다." }""";

    public static final String INVALID_REFRESH_TOKEN = """
        { "code": "INVALID_REFRESH_TOKEN", "message": "유효하지 않은 리프레시 토큰입니다." }""";

    public static final String EXPIRED_REFRESH_TOKEN = """
        { "code": "EXPIRED_REFRESH_TOKEN", "message": "리프레시 토큰이 만료되었습니다." }""";

    public static final String EXPIRED_TOKEN = """
        { "code": "EXPIRED_TOKEN", "message": "토큰이 만료되었습니다." }""";

    // 403
    public static final String FORBIDDEN = """
        { "code": "FORBIDDEN", "message": "권한이 없습니다." }""";

    public static final String ACCESS_DENIED = """
        { "code": "ACCESS_DENIED", "message": "권한이 없습니다." }""";

    // 404
    public static final String USER_NOT_FOUND = """
        { "code": "NOTFOUND_USER", "message": "해당 사용자를 찾을 수 없습니다." }""";

    public static final String OAUTH_ID_NOT_FOUND = """
        { "code": "OAUTH_ID_NOT_FOUND", "message": "OAuth ID를 찾을 수 없습니다." }""";

    // 500
    public static final String INTERNAL_SERVER_ERROR = """
        { "code": "INTERNAL_SERVER_ERROR", "message": "서버 오류가 발생했습니다." }""";

    public static final String PROFILE_NOT_FOUND = """
      { "code": "NOT_FOUND_PROFILE", "message": "프로필을 찾을 수 없습니다." }""";

    public static final String INVALID_CAREER_CODE = """
      { "code": "INVALID_CAREER_CODE", "message": "유효하지 않은 경력 코드입니다." }""";


    public static final String PROFILE_INVALID_PARAMETER = """
      {
        "code": "INVALID_PARAMETER",
        "message": "유효하지 않은 요청 파라미터가 포함되어 있습니다.",
        "errors": [
          {"field":"basic","rejectedValue":null,"reason":"must not be null"},
          {"field":"careerCode","rejectedValue":null,"reason":"must not be null"},
          {"field":"abilities","rejectedValue":null,"reason":"must not be null"}
        ]
      }""";

    //AI 서버 관련
    public static final String AI_CALL_FAILURE = """
      { "code":"AI_CALL_FAILURE","message":"AI 추천 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요." }""";
}