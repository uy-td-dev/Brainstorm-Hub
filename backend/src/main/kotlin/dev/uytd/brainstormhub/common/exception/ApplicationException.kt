package dev.uytd.brainstormhub.common.exception

import org.springframework.http.HttpStatus

abstract class ApplicationException(
    // Use 'override' to correctly implement the message from RuntimeException
    override val message: String,
    open val errorCode: String,
    open val httpStatus: HttpStatus
) : RuntimeException(message) {

    // Subclasses can override this to provide more specific details.
    open val details: List<String>
        get() = emptyList()
}