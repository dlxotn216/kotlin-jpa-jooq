package io.taesu.ktjpajooq.base.interfaces

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
    @ExceptionHandler(Exception::class)
    fun handleUserEmailDuplicatedException(e: Exception): ResponseEntity<FailResponse> {
        log.error("error ${e.message}", e)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FailResponse.from(e.message))
    }
}
