package dev.uytd.brainstormhub.usermanagement.domain.exception

import dev.uytd.brainstormhub.common.exception.ApplicationException
import org.springframework.http.HttpStatus

class UserAlreadyExistsException(email: String) : ApplicationException(
    message = "User with email '$email' already exists.",
    errorCode = "USER_ALREADY_EXISTS",
    httpStatus = HttpStatus.CONFLICT // HttpStatus.CONFLICT (409) is more semantic for this error
)