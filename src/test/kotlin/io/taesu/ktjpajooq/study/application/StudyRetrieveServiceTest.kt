package io.taesu.ktjpajooq.study.application

import com.nhaarman.mockitokotlin2.doReturn
import io.taesu.ktjpajooq.base.exception.InvalidRequestException
import io.taesu.ktjpajooq.study.domain.Study
import io.taesu.ktjpajooq.study.domain.StudyRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
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
internal class StudyRetrieveServiceTest {
    @Mock
    lateinit var studyRepository: StudyRepository

    @InjectMocks
    @Spy
    lateinit var studyRetrieveService: StudyRetrieveService

    @Test
    fun `should success to retrieve study`() {
        // given
        val studyKey = 1L
        val study = Study(key = studyKey, id = "STD_NAME", name = "origin name")

        doReturn(Optional.of(study)).`when`(studyRepository).findById(studyKey)

        // when
        val response = studyRetrieveService.retrieve(studyKey)

        // then
        with(response) {
            assertThat(key).isEqualTo(studyKey)
            assertThat(id).isEqualTo("STD_NAME")
            assertThat(name).isEqualTo("origin name")
            assertThat(deleted).isEqualTo(false)
        }
    }

    @Test
    fun `should throw when retrieve not exists study`() {
        // given
        val studyKey = 1L
        doReturn(Optional.ofNullable(null)).`when`(studyRepository).findById(studyKey)

        // when
        assertThrows<InvalidRequestException>("Invalid study key") {
            studyRetrieveService.retrieve(studyKey)
        }

    }

}