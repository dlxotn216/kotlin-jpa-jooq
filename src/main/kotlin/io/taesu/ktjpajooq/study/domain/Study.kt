package io.taesu.ktjpajooq.study.domain

import io.taesu.ktjpajooq.user.domain.User
import org.hibernate.annotations.NaturalId
import org.springframework.data.jpa.repository.JpaRepository
import java.io.Serializable
import java.util.*
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
class Study(
        @Id
        @Column(name = "STUDY_KEY")
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STUDY_SEQ")
        @SequenceGenerator(allocationSize = 1, name = "STUDY_SEQ", sequenceName = "STUDY_SEQ")
        var key: Long? = null,

        @Column(name = "STUDY_ID", length = 256, unique = true, nullable = false)
        val id: String,

        @Column(name = "NAME", length = 512, nullable = false)
        val name: String,

        ) {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "study", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    val studyUsers = mutableSetOf<StudyUser>()

    fun addUser(user: User) {
        studyUsers.add(StudyUser(user, this))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Study
        return id == other.id
    }

    override fun hashCode() = id.hashCode()
}

interface StudyRepository : JpaRepository<Study, Long>

@Table(name = "STD_USER")
@IdClass(StudyUserId::class)
@Entity(name = "STUDY_USER")
class StudyUser(
        @Id
        @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        @JoinColumn(name = "USER_ID")
        val user: User,

        @Id
        @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
        @JoinColumn(name = "STUDY_ID")
        val study: Study
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StudyUser
        return other.study == study && other.user == user
    }

    override fun hashCode() = study.hashCode() + 19 * user.hashCode()
}

data class StudyUserId(
        val user: Long,
        val study: Long
) : Serializable {
    constructor() : this(-1L, -1L)
}