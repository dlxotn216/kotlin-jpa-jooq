package io.taesu.ktjpajooq.study.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * Created by itaesu on 2021/03/16.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
internal class StudyUserIdTest {
    @Test
    fun `should create with default value`() {
        // when
        val id = StudyUserId()

        // then
        assertThat(id).isEqualTo(StudyUserId())
        assertThat(id.user).isEqualTo(1L)
        assertThat(id.study).isEqualTo(-1L)
    }
}