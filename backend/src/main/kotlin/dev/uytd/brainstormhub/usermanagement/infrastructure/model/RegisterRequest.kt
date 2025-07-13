package dev.uytd.brainstormhub.usermanagement.infrastructure.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String?
)
