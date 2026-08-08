package com.fenil.projecthub.common.security;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieService {

    private static final String REFRESH_COOKIE =
            "refresh_token";

    private final JwtProperties properties;

    public AuthCookieService(
            JwtProperties properties
    ) {
        this.properties = properties;
    }

    public ResponseCookie createRefreshCookie(
            String refreshToken
    ) {
        return ResponseCookie
                .from(REFRESH_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(properties.refreshTokenTtl())
                .build();
    }

    public ResponseCookie deleteRefreshCookie() {
        return ResponseCookie
                .from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(0)
                .build();
    }
}