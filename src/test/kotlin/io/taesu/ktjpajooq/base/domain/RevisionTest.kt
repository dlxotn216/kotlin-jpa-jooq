package io.taesu.ktjpajooq.base.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Created by itaesu on 2021/03/16.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
internal class RevisionTest {

    @Test
    fun `should match LocalDateTime`() {
        // given
        val baseDateTime = LocalDateTime.of(2021, 3, 16, 12, 12, 12)
        val revision = Revision(1L, baseDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli())

        // when
        val revisionDate = revision.getRevisionDate()

        // then
        assertThat(revisionDate).isEqualTo(baseDateTime)
    }
}