package io.taesu.ktjpajooq.study.application

import io.taesu.ktjpajooq.base.exception.throwResourceNotFound
import io.taesu.ktjpajooq.study.domain.StudyRepository
import io.taesu.ktjpajooq.study.interfaces.StudyRetrieveResponse
import org.springframework.stereotype.Component

@Component
class StudyRetrieveService(
        private val studyRepository: StudyRepository
) {
    fun retrieve(studyKey: Long): StudyRetrieveResponse {
        return studyRepository.findById(studyKey).map {
            StudyRetrieveResponse(
                    key = it.key!!,
                    id = it.id,
                    name = it.name,
                    deleted = it.deleted
            )
        }.orElseThrow { throwResourceNotFound() }
    }
}