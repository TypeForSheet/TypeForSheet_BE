package com.typeforsheet.backend.domain.auth.service;

import com.typeforsheet.backend.domain.auth.dto.SocialLoginResponse;
import com.typeforsheet.backend.domain.auth.oauth.KakaoTokenVerifier;
import com.typeforsheet.backend.domain.user.entity.SocialProvider;
import com.typeforsheet.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

  private final KakaoTokenVerifier kakaoTokenVerifier;
  private final AuthService authService;
  private final RefreshTokenService refreshTokenService;

  public SocialLoginResponse login(String accessToken) {

    String kakaoUserId = kakaoTokenVerifier.verifyAndGetUserId(accessToken);

    User user = authService.findOrCreateUser(SocialProvider.KAKAO, kakaoUserId);

    RefreshTokenService.TokenPair tokens = refreshTokenService.issueTokens(user);

    return SocialLoginResponse.of(user.getId(), tokens.accessToken(), tokens.refreshToken());
  }
}
