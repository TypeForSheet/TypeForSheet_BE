package com.typeforsheet.backend.domain.auth.oauth;

import com.typeforsheet.backend.global.exception.BusinessException;
import com.typeforsheet.backend.global.exception.ErrorCode;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class KakaoTokenVerifier {

  private static final String TOKEN_INFO_URL = "https://kapi.kakao.com/v1/user/access_token_info";

  private final RestClient restClient;
  private final String kakaoAppId;

  public KakaoTokenVerifier(@Value("${app.oauth.kakao.app-id:}") String kakaoAppId) {

    this.kakaoAppId = kakaoAppId;

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

    requestFactory.setConnectTimeout(Duration.ofSeconds(3));
    requestFactory.setReadTimeout(Duration.ofSeconds(5));

    this.restClient = RestClient.builder().requestFactory(requestFactory).build();
  }

  public String verifyAndGetUserId(String accessToken) {

    if (kakaoAppId.isBlank()) {
      throw new BusinessException(ErrorCode.KAKAO_LOGIN_UNAVAILABLE);
    }

    if (accessToken == null || accessToken.isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_KAKAO_TOKEN);
    }

    try {
      KakaoTokenInfo info =
          restClient
              .get()
              .uri(TOKEN_INFO_URL)
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
              .retrieve()
              .onStatus(
                  HttpStatusCode::is4xxClientError,
                  (request, response) -> {
                    throw new BusinessException(ErrorCode.INVALID_KAKAO_TOKEN);
                  })
              .body(KakaoTokenInfo.class);

      if (info == null
          || info.id() == null
          || info.app_id() == null
          || !kakaoAppId.equals(info.app_id().toString())) {

        throw new BusinessException(ErrorCode.INVALID_KAKAO_TOKEN);
      }

      return info.id().toString();

    } catch (BusinessException exception) {
      throw exception;
    } catch (RestClientException exception) {
      throw new BusinessException(ErrorCode.KAKAO_LOGIN_UNAVAILABLE, exception);
    }
  }

  private record KakaoTokenInfo(Long id, Long app_id) {}
}
