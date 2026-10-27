package com.typeforsheet.backend.domain.auth.service;

import com.typeforsheet.backend.domain.auth.entity.RefreshToken;
import com.typeforsheet.backend.domain.auth.repository.RefreshTokenRepository;
import com.typeforsheet.backend.domain.user.entity.User;
import com.typeforsheet.backend.global.security.JwtTokenProvider;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private static final long REFRESH_TOKEN_DAYS = 14;

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtTokenProvider jwtTokenProvider;

  private final SecureRandom secureRandom = new SecureRandom();

  @Transactional
  public TokenPair issueTokens(User user) {

    if (user == null || user.getId() == null) {
      throw new IllegalArgumentException("Invalid user.");
    }

    String accessToken = jwtTokenProvider.createAccessToken(user.getId());

    String refreshToken = generateRefreshToken();

    RefreshToken entity = new RefreshToken(user, hashToken(refreshToken), expiresAt());

    refreshTokenRepository.save(entity);

    return new TokenPair(accessToken, refreshToken);
  }

  @Transactional
  public TokenPair refresh(String rawRefreshToken) {

    String tokenHash = hashToken(rawRefreshToken);

    RefreshToken storedToken =
        refreshTokenRepository
            .findByTokenHashForUpdate(tokenHash)
            .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token."));

    if (storedToken.isExpired(nowUtc())) {
      throw new IllegalArgumentException("Expired refresh token.");
    }

    User user = storedToken.getUser();

    String newAccessToken = jwtTokenProvider.createAccessToken(user.getId());

    String newRefreshToken = generateRefreshToken();

    storedToken.rotate(hashToken(newRefreshToken), expiresAt());

    return new TokenPair(newAccessToken, newRefreshToken);
  }

  @Transactional
  public void revoke(String rawRefreshToken, Long authenticatedUserId) {

    if (authenticatedUserId == null) {
      throw new IllegalArgumentException("Authenticated user is required.");
    }

    String tokenHash = hashToken(rawRefreshToken);

    RefreshToken storedToken =
        refreshTokenRepository.findByTokenHashForUpdate(tokenHash).orElse(null);

    if (storedToken == null) {
      return;
    }

    Long tokenOwnerId = storedToken.getUser().getId();

    if (!tokenOwnerId.equals(authenticatedUserId)) {
      throw new SecurityException("Refresh token does not belong to the authenticated user.");
    }

    refreshTokenRepository.delete(storedToken);
  }

  private String generateRefreshToken() {

    byte[] bytes = new byte[32];
    secureRandom.nextBytes(bytes);

    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private String hashToken(String token) {

    if (token == null || token.isBlank()) {
      throw new IllegalArgumentException("Refresh token is required.");
    }

    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));

      return HexFormat.of().formatHex(hashed);

    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is unavailable.", e);
    }
  }

  private LocalDateTime nowUtc() {
    return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
  }

  private LocalDateTime expiresAt() {
    return LocalDateTime.ofInstant(
        Instant.now().plusSeconds(REFRESH_TOKEN_DAYS * 24 * 60 * 60), ZoneOffset.UTC);
  }

  public record TokenPair(String accessToken, String refreshToken) {}
}
