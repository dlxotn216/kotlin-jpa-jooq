package io.taesu.ktjpajooq.role.interfaces

import io.taesu.ktjpajooq.base.interfaces.SuccessResponse
import io.taesu.ktjpajooq.role.application.RoleUpdateService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

/**
 * Created by itaesu on 2021/03/17.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@RestController
class RoleUpdateController(
        private val roleUpdateService: RoleUpdateService
) {

    @PutMapping("/api/v1/roles/{roleKey}")
    fun create(
        @PathVariable("roleKey") roleKey: Long,
        @RequestBody request: RoleUpdateRequest): ResponseEntity<SuccessResponse<RoleRetrieveResponse>> {
        return ResponseEntity.created(URI.create("/api/v1/roles/"))
                .body(SuccessResponse(result = roleUpdateService.update(roleKey, request)))
    }
}

class RoleUpdateRequest(
        val name: String,
        val reason: String,
        val deleted: Boolean
)