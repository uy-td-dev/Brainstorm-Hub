package dev.uytd.brainstormhub.usermanagement.infrastructure.model

data class LoginRequest(
    val email: String,
    val password: String
)
