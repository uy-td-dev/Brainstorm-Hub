package dev.uytd.brainstormhub.usermanagement.domain.exception

import dev.uytd.brainstormhub.common.exception.ApplicationException
import org.springframework.http.HttpStatus


class InvalidCredentialsException(email: String) : ApplicationException(
    message = "Invalid credentials for user $email",
    errorCode = "INVALID_CREDENTIALS",
    httpStatus = HttpStatus.UNAUTHORIZED,
)