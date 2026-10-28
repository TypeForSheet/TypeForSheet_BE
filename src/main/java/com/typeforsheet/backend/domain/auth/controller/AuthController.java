package com.typeforsheet.backend.domain.auth.controller;

import com.typeforsheet.backend.domain.auth.dto.AppleLoginRequest;
import com.typeforsheet.backend.domain.auth.dto.AppleNonceResponse;
import com.typeforsheet.backend.domain.auth.dto.GoogleLoginRequest;
import com.typeforsheet.backend.domain.auth.dto.KakaoLoginRequest;
import com.typeforsheet.backend.domain.auth.dto.RefreshTokenRequest;
import com.typeforsheet.backend.domain.auth.dto.SocialLoginResponse;
import com.typeforsheet.backend.domain.auth.dto.TokenResponse;
import com.typeforsheet.backend.domain.auth.service.AppleAuthService;
import com.typeforsheet.backend.domain.auth.service.AppleNonceService;
import com.typeforsheet.backend.domain.auth.service.GoogleAuthService;
import com.typeforsheet.backend.domain.auth.service.KakaoAuthService;
import com.typeforsheet.backend.domain.auth.service.RefreshTokenService;
import com.typeforsheet.backend.global.exception.BusinessException;
import com.typeforsheet.backend.global.exception.ErrorCode;
import com.typeforsheet.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원가입 및 로그인", description = "Google, Kakao, Apple 로그인 및 JWT 인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final GoogleAuthService googleAuthService;
  private final KakaoAuthService kakaoAuthService;
  private final AppleAuthService appleAuthService;
  private final AppleNonceService appleNonceService;
  private final RefreshTokenService refreshTokenService;

  @Operation(
      summary = "구글 소셜 로그인 및 자동 회원가입",
      description =
          """
                    Google ID Token을 검증하여 로그인합니다.

                    - 신규 회원은 자동 가입됩니다.
                    - 기존 회원은 동일 계정으로 로그인합니다.
                    - TFS Access Token과 Refresh Token을 반환합니다.
                    """)
  @PostMapping("/login/google")
  public ResponseEntity<ApiResponse<SocialLoginResponse>> googleLogin(
      @Valid @RequestBody GoogleLoginRequest request) {

    SocialLoginResponse response = googleAuthService.login(request.idToken());

    return ResponseEntity.ok(ApiResponse.success("구글 로그인에 성공했습니다.", response));
  }

  @Operation(
      summary = "카카오 소셜 로그인 및 자동 회원가입",
      description =
          """
                    Kakao Access Token을 검증하여 로그인합니다.

                    - 유효한 Kakao Access Token이 필요합니다.
                    - TFS에 등록된 카카오 앱의 토큰인지 확인합니다.
                    - 신규 회원은 자동 가입됩니다.
                    - 로그인 성공 시 TFS 토큰을 발급합니다.
                    """)
  @PostMapping("/login/kakao")
  public ResponseEntity<ApiResponse<SocialLoginResponse>> kakaoLogin(
      @Valid @RequestBody KakaoLoginRequest request) {

    SocialLoginResponse response = kakaoAuthService.login(request.accessToken());

    return ResponseEntity.ok(ApiResponse.success("카카오 로그인에 성공했습니다.", response));
  }

  @Operation(
      summary = "애플 로그인용 nonce 발급",
      description =
          """
                    Apple 로그인을 시작하기 전 일회용 nonce를 발급합니다.

                    - 발급한 nonce는 5분간 유효합니다.
                    - 앱은 원본 nonce를 SHA-256으로 해싱해
                      Apple 인증 요청에 전달합니다.
                    - 이후 로그인 API에는 원본 nonce를 전달합니다.
                    """)
  @GetMapping("/apple/nonce")
  public ResponseEntity<ApiResponse<AppleNonceResponse>> appleNonce() {

    String nonce = appleNonceService.createNonce();

    return ResponseEntity.ok(
        ApiResponse.success("Apple 로그인 nonce 발급에 성공했습니다.", new AppleNonceResponse(nonce)));
  }

  @Operation(
      summary = "애플 소셜 로그인 및 자동 회원가입",
      description =
          """
                    Apple Identity Token과 nonce를 검증하여 로그인합니다.

                    - Apple Identity Token의 서명과 발급자를 검증합니다.
                    - 대상 앱 식별자와 만료 시간을 검증합니다.
                    - 서버에서 발급한 일회용 nonce를 검증합니다.
                    - 신규 회원은 자동 가입됩니다.
                    - TFS Access Token과 Refresh Token을 반환합니다.
                    """)
  @PostMapping("/login/apple")
  public ResponseEntity<ApiResponse<SocialLoginResponse>> appleLogin(
      @Valid @RequestBody AppleLoginRequest request) {

    SocialLoginResponse response = appleAuthService.login(request.identityToken(), request.nonce());

    return ResponseEntity.ok(ApiResponse.success("애플 로그인에 성공했습니다.", response));
  }

  @Operation(
      summary = "Access Token 및 Refresh Token 재발급",
      description =
          """
                    유효한 Refresh Token으로 토큰을 재발급합니다.

                    - Access Token 없이 호출할 수 있습니다.
                    - 기존 Refresh Token은 무효화됩니다.
                    - 새로운 Access Token과 Refresh Token을 반환합니다.
                    """)
  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<TokenResponse>> refresh(
      @Valid @RequestBody RefreshTokenRequest request) {

    RefreshTokenService.TokenPair tokens = refreshTokenService.refresh(request.refreshToken());

    TokenResponse response = TokenResponse.of(tokens.accessToken(), tokens.refreshToken());

    return ResponseEntity.ok(ApiResponse.success("토큰 재발급에 성공했습니다.", response));
  }

  @Operation(
      summary = "로그아웃 및 Refresh Token 폐기",
      description =
          """
                    로그인한 사용자의 Refresh Token을 폐기합니다.

                    - 유효한 TFS Access Token이 필요합니다.
                    - 요청 본문에 Refresh Token을 전달합니다.
                    - 해당 사용자 소유의 토큰만 폐기할 수 있습니다.
                    - 기존 Access Token은 만료 전까지 유효할 수 있습니다.
                    """)
  @SecurityRequirement(name = "bearerAuth")
  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody RefreshTokenRequest request) {

    if (jwt == null) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED);
    }

    if (!"access".equals(jwt.getClaimAsString("token_type"))) {
      throw new BusinessException(ErrorCode.INVALID_ACCESS_TOKEN);
    }

    Long userId;

    try {
      userId = Long.valueOf(jwt.getSubject());
    } catch (NumberFormatException | NullPointerException e) {
      throw new BusinessException(ErrorCode.INVALID_ACCESS_TOKEN, e);
    }

    refreshTokenService.revoke(request.refreshToken(), userId);

    return ResponseEntity.ok(ApiResponse.success("로그아웃에 성공했습니다."));
  }
}
