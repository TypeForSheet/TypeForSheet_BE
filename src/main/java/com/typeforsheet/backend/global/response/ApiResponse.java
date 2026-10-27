package com.typeforsheet.backend.global.response;

import com.typeforsheet.backend.global.exception.ErrorCode;

public record ApiResponse<T>(boolean success, String code, String message, T data) {

  public static <T> ApiResponse<T> success(String message, T data) {

    return new ApiResponse<>(true, "SUCCESS", message, data);
  }

  public static ApiResponse<Void> success(String message) {

    return new ApiResponse<>(true, "SUCCESS", message, null);
  }

  public static ApiResponse<Void> error(ErrorCode errorCode) {

    return new ApiResponse<>(false, errorCode.name(), errorCode.getMessage(), null);
  }
}
