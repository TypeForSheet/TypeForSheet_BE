package com.typeforsheet.backend.global.exception;

import com.typeforsheet.backend.global.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {

    ErrorCode errorCode = exception.getErrorCode();

    return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.error(errorCode));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
      MethodArgumentNotValidException exception) {

    Map<String, String> errors = new LinkedHashMap<>();

    exception
        .getBindingResult()
        .getFieldErrors()
        .forEach(
            error ->
                errors.putIfAbsent(
                    error.getField(),
                    error.getDefaultMessage() == null
                        ? "유효하지 않은 값입니다."
                        : error.getDefaultMessage()));

    return ResponseEntity.badRequest()
        .body(
            new ApiResponse<>(
                false,
                ErrorCode.INVALID_REQUEST.name(),
                ErrorCode.INVALID_REQUEST.getMessage(),
                errors));
  }

  @ExceptionHandler({HttpMessageNotReadableException.class, ConstraintViolationException.class})
  public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception exception) {

    return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INVALID_REQUEST));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiResponse<Void>> handleResponseStatus(ResponseStatusException exception) {

    HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());

    if (status == HttpStatus.UNAUTHORIZED) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(ErrorCode.UNAUTHORIZED));
    }

    if (status == HttpStatus.FORBIDDEN) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error(ErrorCode.ACCESS_DENIED));
    }

    return ResponseEntity.status(exception.getStatusCode())
        .headers(exception.getHeaders())
        .body(new ApiResponse<>(false, "HTTP_ERROR", "요청을 처리할 수 없습니다.", null));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {

    if (exception instanceof ErrorResponse errorResponse) {

      return ResponseEntity.status(errorResponse.getStatusCode())
          .headers(errorResponse.getHeaders())
          .body(new ApiResponse<>(false, "HTTP_ERROR", "요청을 처리할 수 없습니다.", null));
    }

    log.error("Unhandled server exception", exception);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
  }
}
