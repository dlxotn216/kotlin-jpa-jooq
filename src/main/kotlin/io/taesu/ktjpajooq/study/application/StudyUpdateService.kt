package io.taesu.ktjpajooq.study.application

import io.taesu.ktjpajooq.base.domain.Audit
import io.taesu.ktjpajooq.base.exception.throwResourceNotFound
import io.taesu.ktjpajooq.study.domain.Study
import io.taesu.ktjpajooq.study.domain.StudyRepository
import io.taesu.ktjpajooq.study.domain.StudyUser
import io.taesu.ktjpajooq.study.interfaces.StudyUpdateRequest
import io.taesu.ktjpajooq.user.domain.User
import io.taesu.ktjpajooq.user.domain.UserRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class StudyUpdateService(
        private val studyRepository: StudyRepository,
        private val userRepository: UserRepository
) {
    @Transactional
    fun update(studyKey: Long, request: StudyUpdateRequest): Long {
        val study = studyRepository.findById(studyKey).orElseThrow { throwResourceNotFound() }
        with(request) {
            study.update(name = name, reason = reason)
        }

        val userMap = request.studyUsers.map { it.key to it.deleted }.toMap()
        val requestedUsers = userRepository.findByKeyIn(userMap.keys)

        requestedUsers
                .mapNotNull { mapTo(it, study, userMap[it.key]) }
                .forEach { study.addStudyUser(it) }
        return studyKey
    }

    private fun mapTo(it: User, study: Study, deleted: Boolean?): StudyUser? {
        return if (deleted != null) {
            StudyUser(it, study, Audit(deleted = deleted, reason = study.reason))
        } else {
            null
        }
    }

}
