package dev.uytd.brainstormhub.config

import dev.uytd.brainstormhub.common.exception.ApplicationException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebExchangeBindException
import java.time.Instant
@Component // Thêm annotation này
class ErrorResponseMapper {

    fun toErrorResponse(ex: Throwable): ErrorResponse {
        return when (ex) {
            is ApplicationException -> ErrorResponse(
                errorCode = ex.errorCode,
                message = ex.message,
                details = ex.details,
                timestamp = Instant.now()
            )
            is WebExchangeBindException -> ErrorResponse(
                errorCode = "BAD_REQUEST",
                message = "Validation failed",
                details = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" },
                timestamp = Instant.now()
            )
            is ConstraintViolationException -> ErrorResponse(
                errorCode = "BAD_REQUEST",
                message = "Constraint violation",
                details = ex.constraintViolations.map { it.message },
                timestamp = Instant.now()
            )
            is AccessDeniedException -> ErrorResponse(
                errorCode = "FORBIDDEN",
                message = ex.message ?: "Access denied",
                details = emptyList(),
                timestamp = Instant.now()
            )
            else -> ErrorResponse(
                errorCode = "INTERNAL_SERVER_ERROR",
                message = "An unexpected error occurred",
                details = if (ex.message != null) listOf(ex.message!!) else emptyList(),
                timestamp = Instant.now()
            )
        }
    }

    fun getHttpStatus(ex: Throwable): HttpStatus {
        return when (ex) {
            is ApplicationException -> ex.httpStatus
            is WebExchangeBindException -> HttpStatus.BAD_REQUEST
            is ConstraintViolationException -> HttpStatus.BAD_REQUEST
            is AccessDeniedException -> HttpStatus.FORBIDDEN
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
}

data class ErrorResponse(
    val errorCode: String,
    val message: String,
    val details: List<String>,
    val timestamp: Instant
) {
    fun toResponseEntity(status: HttpStatus): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(status).body(this)
    }
}

data class ErrorEvent(val errorCode: String, val message: String)