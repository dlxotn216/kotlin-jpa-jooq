package io.taesu.ktjpajooq.study.interfaces

import io.taesu.ktjpajooq.base.interfaces.SuccessResponse
import io.taesu.ktjpajooq.study.application.StudyRetrieveService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Created by itaesu on 2021/03/16.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@RestController
class StudyRetrieveController(
        val studyRetrieveService: StudyRetrieveService
) {

    @GetMapping("/api/v1/studies/{studyKey}")
    fun retrieveStudy(@PathVariable studyKey: Long): ResponseEntity<SuccessResponse<StudyRetrieveResponse>> {
        return ResponseEntity.ok().body(SuccessResponse(studyRetrieveService.retrieve(studyKey)))
    }
}

class StudyRetrieveResponse(
        val key: Long,
        val id: String,
        val name: String,
        val deleted: Boolean
)