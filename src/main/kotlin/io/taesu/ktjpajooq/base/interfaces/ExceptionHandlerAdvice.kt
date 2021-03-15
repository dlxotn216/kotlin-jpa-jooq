package io.taesu.ktjpajooq.base.interfaces

import io.taesu.ktjpajooq.base.exception.InvalidRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

/**
 * Created by itaesu on 2021/03/14.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@RestController
@ControllerAdvice
class ExceptionHandlerAdvice {
    private val log: Logger = LoggerFactory.getLogger(ExceptionHandlerAdvice::class.java)

    @ExceptionHandler(InvalidRequestException::class)
    fun handleInvalidRequestException(e: InvalidRequestException): ResponseEntity<FailResponse> {
        return with(e) {
            log.error("error $message", this)
            ResponseEntity.status(statusCode).body(FailResponse(errorCode, message))
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<FailResponse> {
        return with(e) {
            log.error("error $message", this)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FailResponse("INVALID_STATUS", message ?: ""))
        }
    }
}
