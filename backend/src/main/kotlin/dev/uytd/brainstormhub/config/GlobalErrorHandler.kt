package dev.uytd.brainstormhub.config

import kotlinx.coroutines.reactor.mono
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@ControllerAdvice
class GlobalErrorHandler(
    private val errorResponseMapper: ErrorResponseMapper,
    private val eventPublisher: ApplicationEventPublisher
) {

    @ExceptionHandler(Throwable::class)
    suspend fun handleException(
        ex: Throwable,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<ErrorResponse>> = mono {
        val errorResponse = errorResponseMapper.toErrorResponse(ex)
        val httpStatus = errorResponseMapper.getHttpStatus(ex)
        if (httpStatus.is5xxServerError) {
            eventPublisher.publishEvent(ErrorEvent(errorResponse.errorCode, errorResponse.message))
        }
        errorResponse.toResponseEntity(httpStatus)
    }
}