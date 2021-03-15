package io.taesu.ktjpajooq.study.application

import com.nhaarman.mockitokotlin2.verify
import io.taesu.ktjpajooq.base.exception.InvalidRequestException
import io.taesu.ktjpajooq.study.domain.Study
import io.taesu.ktjpajooq.study.domain.StudyRepository
import io.taesu.ktjpajooq.study.interfaces.StudyCreateRequest
import io.taesu.ktjpajooq.user.domain.User
import io.taesu.ktjpajooq.user.domain.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.*
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

/**
 * Created by itaesu on 2021/03/15.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@ExtendWith(MockitoExtension::class)
internal class StudyCreateServiceTest {
    @Mock
    private lateinit var studyRepository: StudyRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Spy
    @InjectMocks
    private lateinit var studyCreateService: StudyCreateService

    @Captor
    private lateinit var studyCaptor: ArgumentCaptor<Study>

    @Test
    fun `should success to create study`() {
        // given
        val id = "test_std"
        val name = "test study name"
        val studyUsers = listOf(1L, 2L)

        `when`(studyRepository.findById(id)).thenReturn(null)
        `when`(userRepository.findByKeyIn(studyUsers)).thenReturn(
                listOf(User(1L, "taesu1", "taesu1@crscube.co.kr", "Lee1"),
                        User(12, "taesu2", "taesu2@crscube.co.kr", "Lee2"))
        )

        val request = StudyCreateRequest(id, name, studyUsers)

        // when
        `when`(studyRepository.save(any())).thenReturn(Study(1L, id, name))
        studyCreateService.create(request)

        // then
        verify(studyRepository).save(studyCaptor.capture())
        val captured = studyCaptor.value
        assertThat(captured.studyUsers.size).isEqualTo(2)
        assertThat(captured.id).isEqualTo(id)
        assertThat(captured.name).isEqualTo(name)
    }

    @Test
    fun `should fail to create study when duplicated id`() {
        // given
        val id = "test_std"
        val name = "test study name"
        val studyUsers = listOf(1L, 2L)

        `when`(studyRepository.findById(id)).thenReturn(Study(1L, id, name))
        val request = StudyCreateRequest(id, name, studyUsers)

        // when
        assertThrows<InvalidRequestException>("Duplicated study id exists") { studyCreateService.create(request) }

        // then
        verify(studyRepository, times(0)).save(any())
    }
}