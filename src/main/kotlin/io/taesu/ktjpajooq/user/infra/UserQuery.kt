package io.taesu.ktjpajooq.user.infra

import io.taesu.ktjpajooq.tables.records.RevinfoRecord
import io.taesu.ktjpajooq.tables.records.UsrRoleHisRecord
import io.taesu.ktjpajooq.tables.records.UsrRoleRecord
import io.taesu.ktjpajooq.tables.references.USR_ROLE
import io.taesu.ktjpajooq.user.domain.UserRoleEntity
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
class UserQuery(private val dslContext: DSLContext) {

    @Transactional
    fun saveUserRoles(userRoles: List<UserRoleEntity>) {
        userRoles.forEach { saveUserRole(it) }
    }

    fun saveUserRole(userRole: UserRoleEntity) {
        val ur = USR_ROLE

        val updated = dslContext.insertInto(ur)
            .set(toRecord(userRole))
            .onDuplicateKeyUpdate()
            .set(ur.DELETED, userRole.deleted)
            .set(ur.REASON, userRole.reason)
            .set(ur.MODIFIED_BY, userRole.modifiedBy)
            .set(ur.MODIFIED_AT, userRole.modifiedAt)
            .where(
                ur.ROLE_KEY.eq(userRole.roleKey)
                    .and(ur.USER_KEY.eq(userRole.userKey))
                    .and(ur.DELETED.ne(userRole.deleted))
            )
            .execute()

        if (updated > 0) {
            dslContext.executeInsert(toHistoryRecord(userRole))
        }
    }

    private fun toRecord(userRole: UserRoleEntity) = UsrRoleRecord(
        userKey = userRole.userKey,
        roleKey = userRole.roleKey,
        deleted = userRole.deleted,
        reason = userRole.reason,
        createdBy = userRole.createdBy,
        createdAt = userRole.createdAt,
        modifiedBy = userRole.modifiedBy,
        modifiedAt = userRole.modifiedAt
    )

    private fun toHistoryRecord(userRole: UserRoleEntity): UsrRoleHisRecord {
        val rev = Math.abs(Random.nextInt()).toLong()
        dslContext.executeInsert(
            RevinfoRecord(rev = rev,
                          revtstmp = userRole.modifiedAt.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()))

        return UsrRoleHisRecord(
            userKey = userRole.userKey,
            roleKey = userRole.roleKey,
            deleted = userRole.deleted,
            reason = userRole.reason,
            createdBy = userRole.createdBy,
            createdAt = userRole.createdAt,
            modifiedBy = userRole.modifiedBy,
            modifiedAt = userRole.modifiedAt,
            rev = rev,
            revtype = 1L
        )
    }

}