package com.typeforsheet.backend.domain.auth.service;

import com.typeforsheet.backend.domain.auth.dto.SocialLoginResponse;
import com.typeforsheet.backend.domain.auth.oauth.GoogleIdTokenVerifier;
import com.typeforsheet.backend.domain.user.entity.SocialProvider;
import com.typeforsheet.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

  private final GoogleIdTokenVerifier googleIdTokenVerifier;
  private final AuthService authService;
  private final RefreshTokenService refreshTokenService;

  public SocialLoginResponse login(String idToken) {

    String googleUserId = googleIdTokenVerifier.verifyAndGetUserId(idToken);

    User user = authService.findOrCreateUser(SocialProvider.GOOGLE, googleUserId);

    RefreshTokenService.TokenPair tokens = refreshTokenService.issueTokens(user);

    return SocialLoginResponse.of(user.getId(), tokens.accessToken(), tokens.refreshToken());
  }
}
