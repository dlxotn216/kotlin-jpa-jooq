package io.taesu.ktjpajooq.user.domain

import org.assertj.core.api.Assertions
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
class UserRepositoryTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should audit entity created when study created`() {
        // given
        val user = User(id = "taesu", email = "taesu@crscube.co.kr", name = "Lee")

        //when
        val savedUser = userRepository.save(user)

        //then
        Assertions.assertThat(savedUser.audit.createdAt).isNotNull
        Assertions.assertThat(savedUser.audit.createdBy).isNotNull
        Assertions.assertThat(savedUser.audit.modifiedAt).isNotNull
        Assertions.assertThat(savedUser.audit.modifiedBy).isNotNull
        Assertions.assertThat(savedUser.audit.deleted).isFalse
        Assertions.assertThat(savedUser.audit.reason).isEqualTo("Initial Input")
    }
}