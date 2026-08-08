package com.fenil.projecthub.auth.controller;

import com.fenil.projecthub.auth.dto.*;
import com.fenil.projecthub.auth.exception.InvalidRefreshTokenException;
import com.fenil.projecthub.auth.service.LoginService;
import com.fenil.projecthub.auth.service.RefreshTokenService;
import com.fenil.projecthub.auth.service.RegistrationService;
import com.fenil.projecthub.common.security.AuthCookieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;
    private final AuthCookieService authCookieService;


    public AuthController(
            RegistrationService registrationService,
            LoginService loginService,
            RefreshTokenService refreshTokenService,
            AuthCookieService authCookieService
    ) {
        this.registrationService = registrationService;
        this.loginService = loginService;
        this.refreshTokenService = refreshTokenService;
        this.authCookieService = authCookieService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(

            @Valid @RequestBody RegisterRequest request

    ) {
        RegisterResponse response = registrationService.register(request);


        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginApiResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse result = loginService.login(request);

        ResponseCookie refreshCookie = authCookieService.createRefreshCookie(
                result.refreshToken()
        );

        LoginApiResponse body = new LoginApiResponse(
                result.accessToken(),
                result.tokenType(),
                result.expiresIn(),
                result.user()
        );

        return ResponseEntity.ok(

        ).header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).body(body);
    }

    @PostMapping("/refresh")

    public ResponseEntity<TokenResponse> refresh(
            @CookieValue(
                    name = "refresh_token",
                    required = false
            )
            String refreshToken
    ) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidRefreshTokenException();
        }

        TokenPair tokenPair = refreshTokenService.rotate(refreshToken);

        ResponseCookie cookie = authCookieService.createRefreshCookie(
                tokenPair.refreshToken()
        );

        TokenResponse body = new TokenResponse(
                tokenPair.accessToken(),
                "Bearer",
                tokenPair.expiresIn()
        );

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(body);

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(
                    name = "refresh_token",
                    required = false
            )
            String refreshToken
    ) {
        if (refreshToken != null &&
                !refreshToken.isBlank()) {
            refreshTokenService.revoke(refreshToken);
        }

        ResponseCookie deleteCookie = authCookieService.deleteRefreshCookie();

        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).build();
    }
}
