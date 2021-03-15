package io.taesu.ktjpajooq.study.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Created by itaesu on 2021/03/15.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@ExtendWith(SpringExtension::class)
@DataJpaTest
class StudyRepositoryTest {
    @Autowired
    private lateinit var studyRepository: StudyRepository

    @Test
    fun `should audit entity created when study created`() {
        // given
        val study = Study(id = "STUDY", name = "My Test Study")

        //when
        val savedStudy = studyRepository.save(study)

        //then
        assertThat(savedStudy.audit.createdAt).isNotNull
        assertThat(savedStudy.audit.createdBy).isNotNull
        assertThat(savedStudy.audit.modifiedAt).isNotNull
        assertThat(savedStudy.audit.modifiedBy).isNotNull
        assertThat(savedStudy.audit.deleted).isFalse
        assertThat(savedStudy.audit.reason).isEqualTo("Initial Input")
    }
}