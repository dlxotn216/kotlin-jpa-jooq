package io.taesu.ktjpajooq.base.interfaces

/**
 * Created by itaesu on 2021/03/14.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
class SuccessResponse(
        val result: Any?,
        val message: String = "Request was success",
) {
    val status: String = "success"
}

class FailResponse(val message: String?) {
    val status = "fail"

    companion object {
        fun from(message: String?): FailResponse {
            return FailResponse(message)
        }
    }
}
