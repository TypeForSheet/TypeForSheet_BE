package com.typeforsheet.backend.domain.auth.dto;

public record SocialLoginResponse(
    Long userId, String accessToken, String refreshToken, String tokenType) {

  public static SocialLoginResponse of(Long userId, String accessToken, String refreshToken) {

    return new SocialLoginResponse(userId, accessToken, refreshToken, "Bearer");
  }
}
