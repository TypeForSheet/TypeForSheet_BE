package com.typeforsheet.backend.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oauth_nonces")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthNonce {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nonce_hash", nullable = false, unique = true, columnDefinition = "CHAR(64)")
  private String nonceHash;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  public OAuthNonce(String nonceHash, LocalDateTime expiresAt) {

    this.nonceHash = nonceHash;
    this.expiresAt = expiresAt;
  }

  public boolean isExpired(LocalDateTime now) {
    return !expiresAt.isAfter(now);
  }
}
