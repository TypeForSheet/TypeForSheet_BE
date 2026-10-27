package com.typeforsheet.backend.domain.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "social_accounts",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_social_provider_user",
          columnNames = {"provider", "provider_user_id"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "provider", nullable = false, length = 20)
  private SocialProvider provider;

  @Column(name = "provider_user_id", nullable = false, length = 255)
  private String providerUserId;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  public SocialAccount(User user, SocialProvider provider, String providerUserId) {
    this.user = user;
    this.provider = provider;
    this.providerUserId = providerUserId;
  }
}
