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
    fun save(role: RoleEntity): RoleEntity {
        return if (role.key <= -1L) {
            insert(role)
        } else {
            update(role)
        }
    }

    @Transactional
    fun insert(role: RoleEntity): RoleEntity {
        val nextVal = ROLE_SEQ.nextval()
        val roleKey = dslContext.select(nextVal).fetchOne(nextVal)
        role.key = roleKey!!

        return role.apply {
            // case 3
            dslContext.batchInsert(toRecord(roleKey, this), toHistoryRecord(roleKey, this)).execute()
        }
    }
    /*
    // case 1
    // val MR = MST_ROLE
    // dslContext.insertInto(MR)
    //         .set(MR.ROLE_KEY, roleKey)
    //         .set(MR.ROLE_ID, role.id)
    //         .set(MR.NAME, role.name)
    //         .set(MR.DELETED, role.deleted)
    //         .set(MR.REASON, role.reason)
    //         .set(MR.CREATED_BY, role.createdBy)
    //         .set(MR.CREATED_AT, role.createdAt)
    //         .set(MR.MODIFIED_BY, role.modifiedBy)
    //         .set(MR.MODIFIED_AT, role.modifiedAt)
    //         .execute()
    //
    // val MR_HIS = MST_ROLE_HIS
    // dslContext.insertInto(MR_HIS)
    //         .set(MR_HIS.ROLE_KEY, roleKey)
    //         .set(MR_HIS.REV, 1L)
    //         .set(MR_HIS.REVTYPE, 1L)
    //         .set(MR_HIS.ROLE_ID, role.id)
    //         .set(MR_HIS.NAME, role.name)
    //         .set(MR_HIS.DELETED, role.deleted)
    //         .set(MR_HIS.REASON, role.reason)
    //         .set(MR_HIS.CREATED_BY, role.createdBy)
    //         .set(MR_HIS.CREATED_AT, role.createdAt)
    //         .set(MR_HIS.MODIFIED_BY, role.modifiedBy)
    //         .set(MR_HIS.MODIFIED_AT, role.modifiedAt)
    //         .execute()

    // case 2
    // dslContext.insertInto(MST_ROLE).set(toRecord(roleKey, role)).execute()
    // dslContext.insertInto(MST_ROLE_HIS).set(toHistoryRecord(roleKey, role)).execute()
     */

    @Transactional
    fun update(role: RoleEntity): RoleEntity {
        return role.apply {
            val updated = dslContext.executeUpdate(toRecord(this.key, this))
            if (updated == 1) {
                dslContext.executeInsert(toHistoryRecord(this.key, this))
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