package com.typeforsheet.backend.domain.auth.service;

import com.typeforsheet.backend.domain.auth.entity.OAuthNonce;
import com.typeforsheet.backend.domain.auth.repository.OAuthNonceRepository;
import com.typeforsheet.backend.global.exception.BusinessException;
import com.typeforsheet.backend.global.exception.ErrorCode;
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
public class AppleNonceService {

  private final OAuthNonceRepository oauthNonceRepository;

  private final SecureRandom secureRandom = new SecureRandom();

  @Transactional
  public String createNonce() {

    byte[] bytes = new byte[32];
    secureRandom.nextBytes(bytes);

    String nonce = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

    LocalDateTime expiresAt =
        LocalDateTime.ofInstant(Instant.now().plusSeconds(300), ZoneOffset.UTC);

    oauthNonceRepository.save(new OAuthNonce(hash(nonce), expiresAt));

    return nonce;
  }

  @Transactional
  public void consumeNonce(String nonce) {

    if (nonce == null || nonce.isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_APPLE_TOKEN);
    }

    OAuthNonce stored =
        oauthNonceRepository
            .findByNonceHashForUpdate(hash(nonce))
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_APPLE_TOKEN));

    LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);

    if (stored.isExpired(now)) {
      throw new BusinessException(ErrorCode.INVALID_APPLE_TOKEN);
    }

    oauthNonceRepository.delete(stored);
  }

  public String hash(String value) {

    try {
      byte[] digest =
          MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));

      return HexFormat.of().formatHex(digest);

    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 unavailable.", exception);
    }
  }
}
