package io.taesu.ktjpajooq.base.domain

import org.hibernate.envers.RevisionEntity
import org.hibernate.envers.RevisionNumber
import org.hibernate.envers.RevisionTimestamp
import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime
import javax.persistence.*


/**
 * Created by itaesu on 2021/03/15.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@RevisionEntity
@Table(name = "REVINFO")
class Revision(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REV_SEQ")
        @SequenceGenerator(name = "REV_SEQ", sequenceName = "REV_SEQ")
        @RevisionNumber
        @Column(name = "REV")
        private val key: Long,

        @RevisionTimestamp
        @Column(name = "REVTSTMP")
        private val timestamp: Long
) : Serializable {
    companion object {
        const val serialVersionUID = 1L
    }

    @Transient
    fun getRevisionDate(): LocalDateTime {
        return LocalDateTime.from(Instant.ofEpochMilli(timestamp))
    }
}