package com.jifelog.auth.application.command

data class ConfirmEmailVerificationCommand(
    val email: String,
    val token: String
)
