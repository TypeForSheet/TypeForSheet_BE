package com.typeforsheet.backend.domain.auth.service;

import com.typeforsheet.backend.domain.auth.repository.OAuthNonceRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OAuthNonceCleanupJob {

  private final OAuthNonceRepository oauthNonceRepository;

  @Scheduled(fixedDelay = 3600000)
  @Transactional
  public void cleanupExpiredNonces() {

    LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);

    oauthNonceRepository.deleteExpiredNonces(now);
  }
}
