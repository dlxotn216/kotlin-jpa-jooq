package io.taesu.ktjpajooq.study.interfaces

import io.taesu.ktjpajooq.base.interfaces.SuccessResponse
import io.taesu.ktjpajooq.study.application.StudyCreateService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

/**
 * Created by itaesu on 2021/03/14.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@RestController
class StudyCreateController(
        val studyCreateService: StudyCreateService
) {
    @PostMapping("/api/v1/studies")
    fun create(@RequestBody request: StudyCreateRequest): ResponseEntity<SuccessResponse> {
        val studyKey = studyCreateService.create(request)
        return ResponseEntity.created(
                URI.create("/api/v1/studies/${studyKey}")
        ).body(SuccessResponse(result = mapOf("studyKey" to studyKey)))
    }
}

class StudyCreateRequest(
        val id: String,
        val name: String,
        val studyUsers: List<Long> = listOf())
