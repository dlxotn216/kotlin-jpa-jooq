package io.taesu.ktjpajooq.user.domain

import io.taesu.ktjpajooq.study.domain.Study
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

/**
 * Created by itaesu on 2021/03/12.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Table(name = "USR_USER")
@Entity(name = "USER")
class User(
        @Id
        @Column(name = "USER_KEY")
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ")
        @SequenceGenerator(allocationSize = 1, name = "USER_SEQ", sequenceName = "USER_SEQ")
        var key: Long? = null,

        @Column(name = "USER_ID", length = 256, unique = true, nullable = false)
        val id: String,

        @Column(name = "EMAIL", length = 256, unique = true, nullable = false)
        val email: String,

        @Column(name = "NAME", length = 512, nullable = false)
        val name: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User
        return id == other.id
    }

    override fun hashCode() = id.hashCode()
}

interface UserRepository : JpaRepository<User, Long> {
    fun findByKeyIn(keys: List<Long>): List<User>
}