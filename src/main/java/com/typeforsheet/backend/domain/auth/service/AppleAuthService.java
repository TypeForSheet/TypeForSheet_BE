package com.typeforsheet.backend.domain.auth.service;

import com.typeforsheet.backend.domain.auth.dto.SocialLoginResponse;
import com.typeforsheet.backend.domain.auth.oauth.AppleIdTokenVerifier;
import com.typeforsheet.backend.domain.user.entity.SocialProvider;
import com.typeforsheet.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppleAuthService {

  private final AppleIdTokenVerifier appleIdTokenVerifier;
  private final AppleNonceService appleNonceService;
  private final AuthService authService;
  private final RefreshTokenService refreshTokenService;

  @Transactional
  public SocialLoginResponse login(String identityToken, String nonce) {

    String appleUserId = appleIdTokenVerifier.verifyAndGetUserId(identityToken, nonce);

    appleNonceService.consumeNonce(nonce);

    User user = authService.findOrCreateUser(SocialProvider.APPLE, appleUserId);

    RefreshTokenService.TokenPair tokens = refreshTokenService.issueTokens(user);

    return SocialLoginResponse.of(user.getId(), tokens.accessToken(), tokens.refreshToken());
  }
}
