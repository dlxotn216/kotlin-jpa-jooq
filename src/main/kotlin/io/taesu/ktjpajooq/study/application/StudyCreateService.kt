package io.taesu.ktjpajooq.study.application

import io.taesu.ktjpajooq.base.exception.InvalidRequestException
import io.taesu.ktjpajooq.base.exception.throwException
import io.taesu.ktjpajooq.study.domain.Study
import io.taesu.ktjpajooq.study.domain.StudyRepository
import io.taesu.ktjpajooq.study.interfaces.StudyCreateRequest
import io.taesu.ktjpajooq.user.domain.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Created by itaesu on 2021/03/14.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Component
class StudyCreateService(
        val studyRepository: StudyRepository,
        val userRepository: UserRepository
) {
    @Transactional
    fun create(request: StudyCreateRequest): Long {
        studyRepository.findById(request.id)?.let {
            throwException("${request.id} is Duplicated study id", HttpStatus.CONFLICT)
        }
        val study = with(request) { Study(id = id, name = name) }
        userRepository.findByKeyIn(request.studyUsers).forEach { study += it }

        return studyRepository.save(study).key!!
    }
}