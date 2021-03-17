package io.taesu.ktjpajooq.role.domain

import com.sun.org.apache.xpath.internal.operations.Bool
import io.taesu.ktjpajooq.base.domain.Audit
import io.taesu.ktjpajooq.study.domain.Study
import org.hibernate.envers.Audited
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.*

/**
 * Created by itaesu on 2021/03/17.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Table(name = "MST_ROLE")
@Entity(name = "ROLE")
@Audited
@EntityListeners(value = [AuditingEntityListener::class])
class Role(
    @Id
    @Column(name = "ROLE_KEY")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROLE_SEQ")
    @SequenceGenerator(allocationSize = 1, name = "ROLE_SEQ", sequenceName = "ROLE_SEQ")
    var key: Long? = null,

    @Column(name = "ROLE_ID", length = 256, unique = true, nullable = false)
    val id: String,

    @Column(name = "NAME", length = 512, nullable = false)
    var name: String,

    @Embedded
    val audit: Audit = Audit()
) {
    val deleted: Boolean get() = audit.deleted
    val reason: String get() = audit.reason
    val createdBy: Long get() = audit.createdBy
    val createdAt: LocalDateTime get() = audit.createdAt
    val modifiedBy: Long get() = audit.modifiedBy
    val modifiedAt: LocalDateTime get() = audit.modifiedAt

    fun update(name: String, reason: String) {
        if (this.name === name) {
            return
        }

        this.name = name
        this.audit.update(reason)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Study
        return id == other.id
    }

    override fun hashCode() = id.hashCode()
}

data class RoleEntity(
    var key: Long = -1L,
    val id: String,
    var name: String,               // 변경 감지 대상 필드
    var deleted: Boolean = false    // 변경 감지 대상 필드
) {
    var audit: Audit = Audit()
    val reason: String get() = audit.reason
    val createdBy: Long get() = audit.createdBy
    val createdAt: LocalDateTime get() = audit.createdAt
    val modifiedBy: Long get() = audit.modifiedBy
    val modifiedAt: LocalDateTime get() = audit.modifiedAt
}