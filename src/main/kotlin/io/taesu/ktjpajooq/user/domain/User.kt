package io.taesu.ktjpajooq.user.domain

import io.taesu.ktjpajooq.base.domain.Audit
import io.taesu.ktjpajooq.role.domain.Role
import io.taesu.ktjpajooq.study.domain.StudyUserId
import org.hibernate.annotations.NaturalId
import org.hibernate.envers.Audited
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.jpa.repository.JpaRepository
import java.io.Serializable
import java.time.LocalDateTime
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
@Audited
@EntityListeners(value = [AuditingEntityListener::class])
class User(
    @Id
    @Column(name = "USER_KEY")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ")
    @SequenceGenerator(allocationSize = 1, name = "USER_SEQ", sequenceName = "USER_SEQ")
    var key: Long? = null,

    @Column(name = "USER_ID", length = 256, unique = true, nullable = false)
    val id: String,

    @NaturalId
    @Column(name = "EMAIL", length = 256, unique = true, nullable = false)
    val email: String,

    @Column(name = "NAME", length = 512, nullable = false)
    val name: String
) {

    @Embedded
    val audit: Audit = Audit()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User
        return id == other.id
    }

    override fun hashCode() = id.hashCode()
}

@Table(name = "USR_ROLE")
@IdClass(UserRoleId::class)
@Entity(name = "USER_ROLE")
@Audited
@EntityListeners(value = [AuditingEntityListener::class])
class UserRole(
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "USER_KEY")
    val user: User,

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "ROLE_KEY")
    val role: Role,

    @Embedded
    val audit: Audit = Audit()
) : Serializable {
    val deleted: Boolean get() = audit.deleted
    val reason: String get() = audit.reason

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserRole
        return other.role == role && other.user == user
    }

    override fun hashCode() = role.hashCode() + 19 * user.hashCode()

    fun update(deleted: Boolean, reason: String) {
        when (deleted) {
            true -> this.delete(reason)
            false -> this.restore(reason)
        }
    }

    fun delete(reason: String) = audit.delete(reason)
    fun restore(reason: String) = audit.restore(reason)
}

data class UserRoleId(
        val user: Long,
        val role: Long
) : Serializable {
    constructor() : this(-1L, -1L)

    companion object {
        const val serialVersionUID = 1L
    }
}


interface UserRepository : JpaRepository<User, Long> {
    fun findByKeyIn(keys: Collection<Long>): List<User>
}

data class UserRoleEntity(
    var userKey: Long = -1L,
    var roleKey: Long = -1L,
    var deleted: Boolean = false    // 변경 감지 대상 필드
) {
    var audit: Audit = Audit()
    val reason: String get() = audit.reason
    val createdBy: Long get() = audit.createdBy
    val createdAt: LocalDateTime get() = audit.createdAt
    val modifiedBy: Long get() = audit.modifiedBy
    val modifiedAt: LocalDateTime get() = audit.modifiedAt
}