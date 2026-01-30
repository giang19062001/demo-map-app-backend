package com.vietq.demo_map_app_backend.utils

import com.vietq.demo_map_app_backend.service.PaymentService
import org.apache.coyote.BadRequestException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    fun handle(ex: Exception): ResponseEntity<ErrorResponse> {
        ex.printStackTrace()
        val error = ErrorResponse(
            success = false,
            data = null,
            statusCode = ExceptionErrorEnum.SERVER_ERROR.status.value(),
            message = ex.message ?: ExceptionErrorEnum.SERVER_ERROR.defaultMessage
        )
        return ResponseEntity.status(ExceptionErrorEnum.SERVER_ERROR.status).body(error)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(ex: BadRequestException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            success = false,
            data = null,
            statusCode = ExceptionErrorEnum.BAD_REQUEST.status.value(),
            message = ex.message ?: ExceptionErrorEnum.BAD_REQUEST.defaultMessage
        )
        return ResponseEntity.status(ExceptionErrorEnum.BAD_REQUEST.status).body(error)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        val error = ErrorResponse(
            success = false,
            data = null,
            statusCode = HttpStatus.BAD_REQUEST.value(),
            message = message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }
}


enum class ExceptionErrorEnum(val status: HttpStatus, val defaultMessage: String) {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "There is an execution error"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Server error"),
}
