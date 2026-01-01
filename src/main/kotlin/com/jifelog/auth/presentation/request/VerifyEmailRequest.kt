package com.jifelog.auth.presentation.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class VerifyEmailRequest(
    @Email
    val email: String,
    @NotBlank
    val token: String
)
