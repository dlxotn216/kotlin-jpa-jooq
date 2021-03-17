package io.taesu.ktjpajooq.role.application

import io.taesu.ktjpajooq.base.domain.Audit
import io.taesu.ktjpajooq.role.domain.RoleEntity
import io.taesu.ktjpajooq.role.infra.RoleQuery
import io.taesu.ktjpajooq.role.interfaces.RoleCreateRequest
import io.taesu.ktjpajooq.role.interfaces.RoleRetrieveResponse
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class RoleCreateService(val roleQuery: RoleQuery) {
    fun create(request: RoleCreateRequest): RoleRetrieveResponse {
        val now = LocalDateTime.now()
        val audit = Audit(
            createdBy = 1L,
            createdAt = now,
            modifiedBy = 1L,
            modifiedAt = now,
        )
        val role = roleQuery.insert(RoleEntity(id = request.id, name = request.name).apply {
            this.audit = audit
        })

        return RoleRetrieveResponse(
            key = role.key,
            id = role.id,
            name = role.name,
            deleted = role.deleted)
    }

}
