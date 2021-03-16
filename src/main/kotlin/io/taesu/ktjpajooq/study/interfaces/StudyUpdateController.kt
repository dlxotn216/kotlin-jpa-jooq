package io.taesu.ktjpajooq.study.interfaces

import io.taesu.ktjpajooq.base.interfaces.SuccessResponse
import io.taesu.ktjpajooq.study.application.StudyRetrieveService
import io.taesu.ktjpajooq.study.application.StudyUpdateService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created by itaesu on 2021/03/16.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@RestController
class StudyUpdateController(
        private val studyUpdateService: StudyUpdateService,
        private val studyRetrieveService: StudyRetrieveService
) {
    @PutMapping("/api/v1/studies/{studyKey}")
    fun update(@PathVariable("studyKey") studyKey: Long,
               @RequestBody request: StudyUpdateRequest): ResponseEntity<SuccessResponse<StudyRetrieveResponse>> {
        return ok().body(SuccessResponse(
                result = studyRetrieveService.retrieve(studyUpdateService.update(studyKey, request))))
    }
}

class StudyUpdateRequest(
        val name: String,
        val studyUsers: List<StudyUserUpdateRequest>,
        val deleted: Boolean,
        val reason: String
)
class StudyUserUpdateRequest(
        val key: Long,
        val deleted: Boolean
)