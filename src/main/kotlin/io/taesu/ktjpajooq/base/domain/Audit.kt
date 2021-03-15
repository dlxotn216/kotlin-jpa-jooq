package io.taesu.ktjpajooq.base.domain

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EntityListeners

/**
 * Created by itaesu on 2021/03/15.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Embeddable
@EntityListeners(value = [AuditingEntityListener::class])
class Audit(
        @Column(name = "DELETED", nullable = false)
        var deleted: Boolean = false,

        @Column(name = "REASON", length = 1000, nullable = false)
        var reason: String = "Initial Input",

        @CreatedBy
        @Column(name = "CREATED_BY", updatable = false, nullable = false)
        var createdBy: Long = -1L,

        @CreatedDate
        @Column(name = "CREATED_AT", updatable = false, nullable = false)
        var createdAt: LocalDateTime = LocalDateTime.now(),

        @LastModifiedBy
        @Column(name = "MODIFIED_BY", nullable = false)
        var modifiedBy: Long = -1L,

        @LastModifiedDate
        @Column(name = "MODIFIED_AT", nullable = false)
        var modifiedAt: LocalDateTime = LocalDateTime.now()
) : Serializable {
    companion object {
        const val serialVersionUID = 1L
    }
}