package io.taesu.ktjpajooq.study.application

import io.taesu.ktjpajooq.base.domain.Audit
import io.taesu.ktjpajooq.base.exception.throwResourceNotFound
import io.taesu.ktjpajooq.study.domain.StudyRepository
import io.taesu.ktjpajooq.study.domain.StudyUser
import io.taesu.ktjpajooq.study.interfaces.StudyUpdateRequest
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
                .filter { userMap.containsKey(it.key) }
                .map {
                    val audit = if (userMap[it.key]!!) {
                        Audit(deleted = true, reason = study.reason)
                    } else {
                        Audit(deleted = false, reason = study.reason)
                    }
                    StudyUser(it, study, audit)
                }
                .forEach {
                    study.addStudyUser(it)
                }

        studyRepository.save(study)
        return studyKey
    }

}
