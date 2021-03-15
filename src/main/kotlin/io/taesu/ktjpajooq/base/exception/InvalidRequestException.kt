package io.taesu.ktjpajooq.base.exception

import org.springframework.core.NestedRuntimeException
import org.springframework.http.HttpStatus

/**
 * Created by itaesu on 2021/03/15.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
class InvalidRequestException(
        override val message: String,
        val statusCode: HttpStatus = HttpStatus.BAD_REQUEST
) : NestedRuntimeException(message)

fun throwException(message: String,
                   statusCode: HttpStatus = HttpStatus.BAD_REQUEST): Nothing = throw InvalidRequestException(message, statusCode)