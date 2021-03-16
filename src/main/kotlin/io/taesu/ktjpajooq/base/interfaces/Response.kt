package io.taesu.ktjpajooq.base.interfaces

/**
 * Created by itaesu on 2021/03/14.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
class  SuccessResponse<T: Any?>(
        val result: T,
        val message: String = "Request was success",
) {
    val status: String = "success"
}

class FailResponse(
        private val errorCode: String,
        private val message: String) {
    val status = "fail"
    val error: Map<String, Any>
        get() = mapOf("errorCode" to errorCode, "message" to message)
}
