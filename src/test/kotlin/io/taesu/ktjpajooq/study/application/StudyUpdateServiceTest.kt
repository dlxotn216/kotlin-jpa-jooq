package io.taesu.ktjpajooq.study.application

import io.taesu.ktjpajooq.base.domain.Audit
import io.taesu.ktjpajooq.study.domain.Study
import io.taesu.ktjpajooq.study.domain.StudyRepository
import io.taesu.ktjpajooq.study.domain.StudyUser
import io.taesu.ktjpajooq.study.interfaces.StudyUpdateRequest
import io.taesu.ktjpajooq.study.interfaces.StudyUserUpdateRequest
import io.taesu.ktjpajooq.user.domain.User
import io.taesu.ktjpajooq.user.domain.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.anyCollection
import org.mockito.Mockito.doReturn
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

/**
 * Created by itaesu on 2021/03/16.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@ExtendWith(MockitoExtension::class)
internal class StudyUpdateServiceTest {
    @Mock
    lateinit var studyRepository: StudyRepository

    @Mock
    lateinit var userRepository: UserRepository

    @InjectMocks
    @Spy
    lateinit var studyUpdateService: StudyUpdateService

    @Test
    fun `Study users status will be updated correctly`() {
        // given
        val studyKey = 1L
        val request = StudyUpdateRequest(
                name = "Study name",
                studyUsers = listOf(
                        StudyUserUpdateRequest(1L, false),
                        StudyUserUpdateRequest(2L, true),
                        StudyUserUpdateRequest(3L, false)
                ),
                reason = "delete user 2 and add user 3",
                deleted = false
        )
        val user1 = User(key = 1L, id = "taesu1", email = "taesu1@crscube.co.kr", name = "Lee1")
        val user2 = User(key = 2L, id = "taesu2", email = "taesu2@crscube.co.kr", name = "Lee2")
        val user3 = User(key = 3L, id = "taesu3", email = "taesu3@crscube.co.kr", name = "Lee3")

        // 최초에 Base audit으로 Study와 User1, User2가 저장되어있는 상태
        val baseAudit = Audit(deleted = false, reason = "Initial Input")
        val study = Study(key = studyKey, id = "STD_NAME", name = "origin name", baseAudit)
        study.studyUsers.add(StudyUser(user1, study, baseAudit))
        study.studyUsers.add(StudyUser(user2, study, baseAudit))

        doReturn(Optional.of(study)).`when`(studyRepository).findById(studyKey)
        doReturn(listOf(user1, user2, user3)).`when`(userRepository).findByKeyIn(anyCollection())

        // when
        studyUpdateService.update(studyKey, request)

        // then
        assertThat(study.studyUsers.size).isEqualTo(3)

        // 1번 사용자는 그대로
        study.studyUsers.find { it == StudyUser(study = study, user = user1) }?.let {
            assertThat(it.deleted).isEqualTo(baseAudit.deleted)
            assertThat(it.reason).isEqualTo(baseAudit.reason)
        } ?: assertThat(1).isEqualTo(2)

        // 2번 사용자는 삭제
        study.studyUsers.find { it == StudyUser(study = study, user = user2) }?.let {
            assertThat(it.deleted).isEqualTo(true)
            assertThat(it.reason).isEqualTo(request.reason)
        } ?: assertThat(1).isEqualTo(2)

        // 3번 사용자는 신규 등록
        study.studyUsers.find { it == StudyUser(study = study, user = user3) }?.let {
            assertThat(it.deleted).isEqualTo(false)
            assertThat(it.reason).isEqualTo(request.reason)
        } ?: assertThat(1).isEqualTo(2)
    }
}