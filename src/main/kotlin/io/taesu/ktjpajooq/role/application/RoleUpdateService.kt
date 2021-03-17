package io.taesu.ktjpajooq.role.application

import io.taesu.ktjpajooq.base.domain.Audit
import io.taesu.ktjpajooq.base.exception.throwResourceNotFound
import io.taesu.ktjpajooq.role.infra.RoleQuery
import io.taesu.ktjpajooq.role.interfaces.RoleRetrieveResponse
import io.taesu.ktjpajooq.role.interfaces.RoleUpdateRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class RoleUpdateService(val roleQuery: RoleQuery) {
    @Transactional
    fun update(roleKey: Long, request: RoleUpdateRequest): RoleRetrieveResponse {
        val previous = roleQuery.select(roleKey) ?: throwResourceNotFound()

        val now = LocalDateTime.now()
        val role = previous.copy(
            key = roleKey,
            name = request.name,
            deleted = request.deleted).apply {
            audit = Audit(
                createdBy = 1L,
                createdAt = now,
                modifiedBy = 1L,
                modifiedAt = now,
                reason = request.reason
            )
        }

        if (previous != role) {
            roleQuery.update(role)
        }

        return RoleRetrieveResponse(
            key = role.key,
            id = role.id,
            name = role.name,
            deleted = role.deleted)
    }

}
