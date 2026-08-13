package com.fenil.projecthub.auth.service;

import com.fenil.projecthub.auth.domain.RefreshToken;
import com.fenil.projecthub.auth.dto.TokenPair;
import com.fenil.projecthub.auth.exception.InvalidRefreshTokenException;
import com.fenil.projecthub.auth.repository.RefreshTokenRepository;
import com.fenil.projecthub.common.security.JwtProperties;
import com.fenil.projecthub.user.domain.User;
import com.fenil.projecthub.user.domain.UserStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final TokenHashService tokenHashService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, RefreshTokenGenerator refreshTokenGenerator, TokenHashService tokenHashService, JwtService jwtService, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.tokenHashService = tokenHashService;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    //create
    @Transactional
    public TokenPair create(User user) {

        String rawToken = refreshTokenGenerator.generate();

        RefreshToken refreshToken = new RefreshToken(user, tokenHashService.hash(rawToken), Instant.now().plus(jwtProperties.refreshTokenTtl()));

        refreshTokenRepository.save(refreshToken);


        return new TokenPair(jwtService.generateAccessToken(user), rawToken, jwtService.getAccessTokenLifetimeSeconds());
    }

    //rotate
    @Transactional
    public TokenPair rotate(String rawToken) {
        String tokenHash = tokenHashService.hash(rawToken);

        RefreshToken existing = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (existing.isRevoked()) {
            handleTokenReuse(existing);

            throw new InvalidRefreshTokenException();
        }

        if (existing.isExpired()) {
            existing.revoke();

            throw new InvalidRefreshTokenException();
        }

        User user = existing.getUser();

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidRefreshTokenException();
        }

        String newRawToken =
                refreshTokenGenerator.generate();

        RefreshToken replacement =
                new RefreshToken(
                        user,
                        tokenHashService.hash(newRawToken),
                        Instant.now().plus(
                                jwtProperties.refreshTokenTtl()
                        )
                );

        refreshTokenRepository.save(replacement);

        existing.revoke(replacement.getId());

        return new TokenPair(
                jwtService.generateAccessToken(user),
                newRawToken,
                jwtService.getAccessTokenLifetimeSeconds()
        );
    }

    //revoke

    @Transactional
    public void revoke(String rawToken) {
        String tokenHash = tokenHashService.hash(rawToken);

        refreshTokenRepository.findByTokenHash(tokenHash).filter(token -> !token.isRevoked()).ifPresent(RefreshToken::revoke);
    }

    // log out form all

    @Transactional
    public void revokeAllForUser(UUID userId) {

        refreshTokenRepository.revokeAllActiveTokensForUser(
                userId,
                Instant.now()
        );

    }

    private void handleTokenReuse(RefreshToken refreshToken) {
        UUID userId = refreshToken.getUser().getId();

        refreshTokenRepository.revokeAllActiveTokensForUser(
                userId, Instant.now()
        );
    }

}
