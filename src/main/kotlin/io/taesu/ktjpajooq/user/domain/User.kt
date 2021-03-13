package io.taesu.ktjpajooq.user.domain

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
)

interface UserRepository : JpaRepository<User, Long>