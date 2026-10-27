package com.typeforsheet.backend.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AppleLoginRequest(@NotBlank String identityToken, @NotBlank String nonce) {}
