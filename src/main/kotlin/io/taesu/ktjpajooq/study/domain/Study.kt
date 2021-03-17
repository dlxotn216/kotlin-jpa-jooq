package io.taesu.ktjpajooq.study.domain

import io.taesu.ktjpajooq.base.domain.Audit
import io.taesu.ktjpajooq.user.domain.User
import org.hibernate.annotations.NaturalId
import org.hibernate.envers.Audited
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.repository.CrudRepository
import java.io.Serializable
import javax.persistence.*

/**
 * Created by itaesu on 2021/03/14.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Table(name = "STD_STUDY")
@Entity(name = "STUDY")
@Audited
@EntityListeners(value = [AuditingEntityListener::class])
class Study(
        @Id
        @Column(name = "STUDY_KEY")
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STUDY_SEQ")
        @SequenceGenerator(allocationSize = 1, name = "STUDY_SEQ", sequenceName = "STUDY_SEQ")
        var key: Long? = null,

        @NaturalId
        @Column(name = "STUDY_ID", length = 256, unique = true, nullable = false)
        val id: String,

        @Column(name = "NAME", length = 512, nullable = false)
        var name: String,

        @Embedded
        val audit: Audit = Audit()
) {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "study", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    val studyUsers: MutableSet<StudyUser> = mutableSetOf()


    val deleted: Boolean get() = audit.deleted
    val reason: String get() = audit.reason

    fun update(name: String, reason: String) {
        if (this.name === name) {
            return
        }

        this.name = name
        this.audit.update(reason)
    }

    operator fun plus(user: User) = addUser(user)
    operator fun plusAssign(user: User) = addUser(user)

    private fun addUser(user: User) = addStudyUser(StudyUser(user, this))

    operator fun plus(studyUser: StudyUser) = addStudyUser(studyUser)
    operator fun plusAssign(studyUser: StudyUser) = addStudyUser(studyUser)

    fun addStudyUser(newUser: StudyUser) {
        val studyUser = studyUsers.find { it == newUser }
        if (studyUser != null) {
            studyUser.update(deleted = newUser.deleted, reason = newUser.reason)
        } else {
            studyUsers += newUser
        }
    }

    fun contains(studyUser: StudyUser) = studyUsers.contains(studyUser)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Study
        return id == other.id
    }

    override fun hashCode() = id.hashCode()
}

interface StudyRepository : CrudRepository<Study, Long> {
    fun findById(studyId: String): Study?
}

@Table(name = "STD_USER")
@IdClass(StudyUserId::class)
@Entity(name = "STUDY_USER")
@Audited
@EntityListeners(value = [AuditingEntityListener::class])
class StudyUser(
        @Id
        @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        @JoinColumn(name = "USER_KEY")
        val user: User,

        @Id
        @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        @JoinColumn(name = "STUDY_KEY")
        val study: Study,

        @Embedded
        val audit: Audit = Audit()
) : Serializable {
    val deleted: Boolean get() = audit.deleted
    val reason: String get() = audit.reason

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StudyUser
        return other.study == study && other.user == user
    }

    override fun hashCode() = study.hashCode() + 19 * user.hashCode()

    fun update(deleted: Boolean, reason: String) {
        when (deleted) {
            true -> this.delete(reason)
            false -> this.restore(reason)
        }
    }

    fun delete(reason: String) = audit.delete(reason)
    fun restore(reason: String) = audit.restore(reason)
}

data class StudyUserId(
        val user: Long,
        val study: Long
) : Serializable {
    constructor() : this(-1L, -1L)

    companion object {
        const val serialVersionUID = 1L
    }
}
