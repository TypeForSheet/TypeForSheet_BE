package com.typeforsheet.backend.domain.auth.oauth;

import com.typeforsheet.backend.global.exception.BusinessException;
import com.typeforsheet.backend.global.exception.ErrorCode;
import java.util.List;
import java.util.Set;
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
public class GoogleIdTokenVerifier {

  private static final String GOOGLE_JWKS_URI = "https://www.googleapis.com/oauth2/v3/certs";

  private static final Set<String> GOOGLE_ISSUERS =
      Set.of("https://accounts.google.com", "accounts.google.com");

  private final NimbusJwtDecoder googleJwtDecoder;
  private final String googleClientId;

  public GoogleIdTokenVerifier(@Value("${app.oauth.google.client-id:}") String googleClientId) {

    this.googleClientId = googleClientId;

    NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(GOOGLE_JWKS_URI).build();

    OAuth2TokenValidator<Jwt> issuerValidator =
        jwt -> {
          String issuer = jwt.getIssuer() == null ? "" : jwt.getIssuer().toString();

          if (GOOGLE_ISSUERS.contains(issuer)) {
            return OAuth2TokenValidatorResult.success();
          }

          return OAuth2TokenValidatorResult.failure(
              new OAuth2Error("invalid_token", "Invalid Google token issuer.", null));
        };

    OAuth2TokenValidator<Jwt> audienceValidator =
        jwt -> {
          List<String> audiences = jwt.getAudience();

          if (!googleClientId.isBlank()
              && audiences != null
              && audiences.contains(googleClientId)) {

            return OAuth2TokenValidatorResult.success();
          }

          return OAuth2TokenValidatorResult.failure(
              new OAuth2Error("invalid_token", "Invalid Google token audience.", null));
        };

    decoder.setJwtValidator(
        new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefault(), issuerValidator, audienceValidator));

    this.googleJwtDecoder = decoder;
  }

  public String verifyAndGetUserId(String idToken) {

    if (googleClientId.isBlank()) {
      throw new BusinessException(ErrorCode.GOOGLE_LOGIN_UNAVAILABLE);
    }

    if (idToken == null || idToken.isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_GOOGLE_TOKEN);
    }

    try {
      Jwt jwt = googleJwtDecoder.decode(idToken);

      String googleUserId = jwt.getSubject();

      if (googleUserId == null || googleUserId.isBlank()) {
        throw new BusinessException(ErrorCode.INVALID_GOOGLE_TOKEN);
      }

      return googleUserId;

    } catch (JwtException exception) {
      throw new BusinessException(ErrorCode.INVALID_GOOGLE_TOKEN, exception);
    }
  }
}
