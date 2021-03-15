package io.taesu.ktjpajooq.study.domain

import io.taesu.ktjpajooq.base.domain.Audit
import io.taesu.ktjpajooq.user.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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
internal class StudyTest {

    @Test
    fun `should update when deleted study users added`() {
        // given
        val study = Study(1L, "TEST_STD", "Test study")
        val studyUser1 = StudyUser(User(1L, "taesu1", "taesu1@crscube.co.kr", "Taesu1"), study)
        val studyUser2 = StudyUser(User(2L, "taesu2", "taesu2@crscube.co.kr", "Taesu2"), study)
        study.addStudyUser(studyUser1)
        study.addStudyUser(studyUser2)

        // when
        studyUser1.delete("delete user")
        studyUser2.delete("delete user")
        study.addStudyUser(studyUser1)
        study.addStudyUser(studyUser2)

        // then
        assertThat(study.studyUsers.all { it.deleted }).isTrue
        assertThat(study.studyUsers.all { it.reason == "delete user" }).isTrue
        assertThat(study.studyUsers.size).isEqualTo(2)
    }

    @Test
    fun `should update when restored study users added`() {
        // given
        val study = Study(1L, "TEST_STD", "Test study")
        val studyUser1 = StudyUser(User(1L, "taesu1", "taesu1@crscube.co.kr", "Taesu1"), study)
        studyUser1.delete("delete already")
        val studyUser2 = StudyUser(User(2L, "taesu2", "taesu2@crscube.co.kr", "Taesu2"), study)
        studyUser2.delete("delete already")
        study.addStudyUser(studyUser1)
        study.addStudyUser(studyUser2)

        // when
        studyUser1.restore("restore user")
        studyUser2.restore("restore user")
        study.addStudyUser(studyUser1)
        study.addStudyUser(studyUser2)

        // then
        assertThat(study.studyUsers.all { it.deleted }).isFalse
        assertThat(study.studyUsers.all { it.reason == "restore user" }).isTrue
        assertThat(study.studyUsers.size).isEqualTo(2)
    }


    @Test
    fun `should not update when fields are same`() {
        // given
        val audit = spy(Audit())
        val study = Study(1L, "TEST_STD", "Test study", audit)
        val spy = spy(study)

        // when
        spy.update("Test study", "change name")

        // then
        assertThat(spy.reason != "change name").isTrue
        verify(audit, never()).update(anyString())
    }
}