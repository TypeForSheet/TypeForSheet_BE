package com.typeforsheet.backend.global.security;

import com.typeforsheet.backend.global.exception.ErrorCode;
import com.typeforsheet.backend.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JsonMapper objectMapper;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/actuator/health",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/error")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/auth/apple/nonce")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/login/**", "/api/auth/refresh")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            exception ->
                exception
                    .authenticationEntryPoint(
                        (request, response, authException) ->
                            writeError(response, ErrorCode.INVALID_ACCESS_TOKEN))
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) ->
                            writeError(response, ErrorCode.ACCESS_DENIED)))
        .oauth2ResourceServer(
            oauth2 ->
                oauth2
                    .authenticationEntryPoint(
                        (request, response, authException) ->
                            writeError(response, ErrorCode.INVALID_ACCESS_TOKEN))
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) ->
                            writeError(response, ErrorCode.ACCESS_DENIED))
                    .jwt(Customizer.withDefaults()));

    return http.build();
  }

  private void writeError(HttpServletResponse response, ErrorCode errorCode) throws IOException {

    if (response.isCommitted()) {
      return;
    }

    response.setStatus(errorCode.getStatus().value());
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    objectMapper.writeValue(response.getOutputStream(), ApiResponse.error(errorCode));
  }
}
