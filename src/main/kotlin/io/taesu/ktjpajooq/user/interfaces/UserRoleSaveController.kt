package io.taesu.ktjpajooq.user.interfaces

import io.taesu.ktjpajooq.base.interfaces.SuccessResponse
import io.taesu.ktjpajooq.user.application.UserRoleSaveService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created by itaesu on 2021/03/17.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@RestController
class UserRoleSaveController(
    private val userRoleSaveService: UserRoleSaveService
) {

    @PutMapping("/api/v1/users/{userKey}/roles")
    fun saveUserRoles(
        @PathVariable userKey: Long,
        @RequestBody request: UserRoleSaveRequest
    ): ResponseEntity<SuccessResponse<String>> {
        userRoleSaveService.saveUserRoles(userKey, request)
        return ResponseEntity.ok(SuccessResponse("ok"))
    }

}

class UserRoleSaveRequest(
    val roles: List<UserRoleRequest>,
    val reason: String
)

class UserRoleRequest(
    val roleKey: Long,
    val deleted: Boolean
)