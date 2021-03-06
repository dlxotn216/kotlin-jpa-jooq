package io.taesu.ktjpajooq.role.infra

import io.taesu.ktjpajooq.base.domain.Audit
import io.taesu.ktjpajooq.role.domain.RoleEntity
import io.taesu.ktjpajooq.sequences.ROLE_SEQ
import io.taesu.ktjpajooq.tables.records.MstRoleHisRecord
import io.taesu.ktjpajooq.tables.records.MstRoleRecord
import io.taesu.ktjpajooq.tables.records.RevinfoRecord
import io.taesu.ktjpajooq.tables.references.MST_ROLE
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import kotlin.random.Random

/**
 * Created by itaesu on 2021/03/17.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Component
class RoleQuery(val dslContext: DSLContext) {

    @Transactional
    fun insert(role: RoleEntity): RoleEntity {
        val nextVal = ROLE_SEQ.nextval()
        val roleKey = dslContext.select(nextVal).fetchOne(nextVal)
        role.key = roleKey!!

        return role.apply {
            dslContext.batchInsert(toRecord(roleKey, this), toHistoryRecord(roleKey, this)).execute()
        }
    }

    @Transactional
    fun update(old: RoleEntity, role: RoleEntity): RoleEntity {
        if (old == role) {
            return old
        }

        val mr = MST_ROLE
        return role.apply {
            val updated = dslContext.update(mr)
                .set(mr.NAME, name)
                .set(mr.DELETED, deleted)
                .set(mr.REASON, reason)
                .where(mr.ROLE_KEY.eq(key)).execute()

            if (updated > 0) {
                dslContext.executeInsert(toHistoryRecord(key, role))
            }
        }
    }

    @Transactional(readOnly = true)
    fun select(roleKey: Long): RoleEntity? {
        val record = dslContext.selectFrom(MST_ROLE)
            .where(MST_ROLE.ROLE_KEY.eq(roleKey)).fetchOne()
        return record?.toEntity()
    }

    fun MstRoleRecord?.toEntity(): RoleEntity? {
        return if (this == null) {
            null
        } else {
            RoleEntity(
                key = roleKey!!,
                id = roleId!!,
                name = name ?: "",
                deleted = deleted!!
            ).apply {
                this.audit = Audit(
                    reason = reason,
                    createdBy = createdBy,
                    createdAt = createdAt,
                    modifiedBy = modifiedBy,
                    modifiedAt = modifiedAt,
                )
            }
        }
    }

    private fun toRecord(roleKey: Long?, role: RoleEntity) = MstRoleRecord(
        roleKey = roleKey!!,
        roleId = role.id,
        name = role.name,
        deleted = role.deleted,
        reason = role.reason,
        createdBy = role.createdBy,
        createdAt = role.createdAt,
        modifiedBy = role.modifiedBy,
        modifiedAt = role.modifiedAt
    )

    private fun toHistoryRecord(roleKey: Long?, role: RoleEntity): MstRoleHisRecord {
        val rev = Math.abs(Random.nextInt()).toLong()
        dslContext.executeInsert(
            RevinfoRecord(rev = rev,
                          revtstmp = role.modifiedAt.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()))

        return MstRoleHisRecord(
            roleKey = roleKey!!,
            roleId = role.id,
            name = role.name,
            deleted = role.deleted,
            reason = role.reason,
            createdBy = role.createdBy,
            createdAt = role.createdAt,
            modifiedBy = role.modifiedBy,
            modifiedAt = role.modifiedAt,
            rev = rev,
            revtype = 1L
        )
    }
}