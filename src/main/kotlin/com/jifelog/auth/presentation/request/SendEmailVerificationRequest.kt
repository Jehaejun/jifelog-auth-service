package com.jifelog.auth.presentation.request

import jakarta.validation.constraints.Email

data class SendEmailVerificationRequest(
    @Email
    val email: String
)
