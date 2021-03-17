package io.taesu.ktjpajooq.user.application

import io.taesu.ktjpajooq.base.domain.Audit
import io.taesu.ktjpajooq.user.domain.UserRoleEntity
import io.taesu.ktjpajooq.user.infra.UserQuery
import io.taesu.ktjpajooq.user.interfaces.UserRoleSaveRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Created by itaesu on 2021/03/17.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Component
class UserRoleSaveService(private val userQuery: UserQuery) {

    @Transactional
    fun saveUserRoles(userKey: Long, request: UserRoleSaveRequest) {
        val audit = Audit(
            reason = request.reason,
            createdBy = 1L,
            createdAt = LocalDateTime.now(),
            modifiedBy = 1L,
            modifiedAt = LocalDateTime.now(),
        )

        userQuery.saveUserRoles(
            request.roles
                .map {
                    UserRoleEntity(userKey = userKey,
                                   roleKey = it.roleKey,
                                   it.deleted).apply { this.audit = audit }
                }
        )
    }

}