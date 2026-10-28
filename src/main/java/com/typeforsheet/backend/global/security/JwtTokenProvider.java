package com.typeforsheet.backend.global.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;

  @Value("${app.jwt.access-token-expiration-minutes}")
  private long accessTokenExpirationMinutes;

  @Value("${app.jwt.issuer}")
  private String issuer;

  public String createAccessToken(Long userId) {

    Instant now = Instant.now();

    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer(issuer)
            .subject(userId.toString())
            .issuedAt(now)
            .expiresAt(now.plus(accessTokenExpirationMinutes, ChronoUnit.MINUTES))
            .claim("token_type", "access")
            .build();

    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

    return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
  }

  public Long getUserId(String token) {
    Jwt jwt = jwtDecoder.decode(token);

    if (!issuer.equals(jwt.getIssuer().toString())) {
      throw new IllegalArgumentException("Invalid JWT issuer.");
    }

    if (!"access".equals(jwt.getClaimAsString("token_type"))) {
      throw new IllegalArgumentException("Invalid token type.");
    }

    return Long.parseLong(jwt.getSubject());
  }
}
