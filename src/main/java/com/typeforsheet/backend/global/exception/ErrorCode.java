package com.typeforsheet.backend.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),

  INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 Access Token입니다."),

  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),

  EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 Refresh Token입니다."),

  TOKEN_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "Refresh Token의 소유자가 일치하지 않습니다."),

  ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),

  INVALID_GOOGLE_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Google 인증 토큰입니다."),

  GOOGLE_LOGIN_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Google 로그인을 사용할 수 없습니다."),

  INVALID_KAKAO_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Kakao 인증 토큰입니다."),

  KAKAO_LOGIN_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Kakao 로그인을 사용할 수 없습니다."),

  INVALID_APPLE_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Apple 인증 토큰입니다."),

  APPLE_LOGIN_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Apple 로그인을 사용할 수 없습니다."),

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
