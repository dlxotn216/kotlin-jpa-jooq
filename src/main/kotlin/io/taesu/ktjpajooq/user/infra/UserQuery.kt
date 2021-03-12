package io.taesu.ktjpajooq.user.infra

import io.taesu.ktjpajooq.tables.records.UsrUserRecord
import io.taesu.ktjpajooq.tables.references.USR_USER
import org.jooq.DSLContext
import org.springframework.stereotype.Component

/**
 * Created by itaesu on 2021/03/12.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@Component
class UserQuery(
        val dslContext: DSLContext
) {

    fun selectUsers(): List<UserSelectResult> {
        return dslContext.selectFrom(USR_USER)
                .fetchInto(UsrUserRecord::class.java)
                .map {
                    UserSelectResult(
                            key = it.key!!,
                            name = it.name!!
                    )
                }
    }
}

class UserSelectResult(
        val key: Long,
        val name: String
)