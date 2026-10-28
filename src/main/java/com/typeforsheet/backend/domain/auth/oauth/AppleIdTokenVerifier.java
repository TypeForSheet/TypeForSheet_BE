package com.typeforsheet.backend.domain.auth.oauth;

import com.typeforsheet.backend.domain.auth.service.AppleNonceService;
import com.typeforsheet.backend.global.exception.BusinessException;
import com.typeforsheet.backend.global.exception.ErrorCode;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class AppleIdTokenVerifier {

  private static final String APPLE_JWKS_URI = "https://appleid.apple.com/auth/keys";

  private static final String APPLE_ISSUER = "https://appleid.apple.com";

  private final NimbusJwtDecoder decoder;
  private final List<String> clientIds;
  private final AppleNonceService appleNonceService;

  public AppleIdTokenVerifier(
      @Value("${app.oauth.apple.client-ids:}") String clientIds,
      AppleNonceService appleNonceService) {

    this.clientIds =
        Arrays.stream(clientIds.split(","))
            .map(String::trim)
            .filter(value -> !value.isBlank())
            .toList();

    this.appleNonceService = appleNonceService;

    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(APPLE_JWKS_URI).build();

    OAuth2TokenValidator<Jwt> audienceValidator =
        jwt -> {
          boolean valid = jwt.getAudience().stream().anyMatch(this.clientIds::contains);

          if (valid) {
            return OAuth2TokenValidatorResult.success();
          }

          return OAuth2TokenValidatorResult.failure(
              new OAuth2Error("invalid_token", "Invalid Apple audience.", null));
        };

    jwtDecoder.setJwtValidator(
        new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefaultWithIssuer(APPLE_ISSUER), audienceValidator));

    this.decoder = jwtDecoder;
  }

  public String verifyAndGetUserId(String identityToken, String nonce) {

    if (clientIds.isEmpty()) {
      throw new BusinessException(ErrorCode.APPLE_LOGIN_UNAVAILABLE);
    }

    if (identityToken == null || identityToken.isBlank() || nonce == null || nonce.isBlank()) {

      throw new BusinessException(ErrorCode.INVALID_APPLE_TOKEN);
    }

    try {
      Jwt jwt = decoder.decode(identityToken);

      String expectedNonce = appleNonceService.hash(nonce);

      String tokenNonce = jwt.getClaimAsString("nonce");

      if (!expectedNonce.equals(tokenNonce)) {
        throw new BusinessException(ErrorCode.INVALID_APPLE_TOKEN);
      }

      String appleUserId = jwt.getSubject();

      if (appleUserId == null || appleUserId.isBlank()) {

        throw new BusinessException(ErrorCode.INVALID_APPLE_TOKEN);
      }

      return appleUserId;

    } catch (JwtException exception) {
      throw new BusinessException(ErrorCode.INVALID_APPLE_TOKEN, exception);
    }
  }
}
