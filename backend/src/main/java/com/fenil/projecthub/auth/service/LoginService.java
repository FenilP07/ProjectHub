package com.fenil.projecthub.auth.service;

import com.fenil.projecthub.auth.dto.LoginRequest;
import com.fenil.projecthub.auth.dto.LoginResponse;
import com.fenil.projecthub.auth.dto.TokenPair;
import com.fenil.projecthub.auth.exception.AccountUnavailableException;
import com.fenil.projecthub.auth.exception.InvalidCredentialsException;
import com.fenil.projecthub.user.domain.User;
import com.fenil.projecthub.user.domain.UserStatus;
import com.fenil.projecthub.user.mapper.UserMapper;
import com.fenil.projecthub.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service

public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final RefreshTokenService refreshTokenService;

    public LoginService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserMapper userMapper,RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional()
    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountUnavailableException();
        }

        TokenPair tokenPair = refreshTokenService.create(user);

        return new LoginResponse(
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                "Bearer",
                jwtService.getAccessTokenLifetimeSeconds(),
                userMapper.toResponse(user)
        );
    }
}