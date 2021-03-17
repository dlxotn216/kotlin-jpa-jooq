package io.taesu.ktjpajooq.role.interfaces

import io.taesu.ktjpajooq.base.interfaces.SuccessResponse
import io.taesu.ktjpajooq.role.application.RoleCreateService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

/**
 * Created by itaesu on 2021/03/17.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@RestController
class RoleCreateController(
        private val roleCreateService: RoleCreateService
) {

    @PostMapping("/api/v1/roles")
    fun create(@RequestBody request: RoleCreateRequest): ResponseEntity<SuccessResponse<RoleRetrieveResponse>> {
        return ResponseEntity.created(URI.create("/api/v1/roles/"))
                .body(SuccessResponse(result = roleCreateService.create(request)))
    }
}

class RoleCreateRequest(
        val id: String,
        val name: String
)

class RoleRetrieveResponse(
        val key: Long,
        val id: String,
        val name: String,
        val deleted: Boolean
)