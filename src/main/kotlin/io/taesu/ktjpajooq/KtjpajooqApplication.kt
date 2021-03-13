package io.taesu.ktjpajooq

import io.taesu.ktjpajooq.user.domain.User
import io.taesu.ktjpajooq.user.domain.UserRepository
import io.taesu.ktjpajooq.user.infra.UserQuery
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication
class KtjpajooqApplication

fun main(args: Array<String>) {
    runApplication<KtjpajooqApplication>(*args)
}

@Component
class Runner(
        val userRepository: UserRepository,
        val userQuery: UserQuery
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        userRepository.save(
                User(
                        id = "taesu3",
                        name = "Lee3",
                        email = "taesu3@crscube.co.kr"
                ))

        userQuery.selectUsers()
                .forEach {
                    println("${it.name}, ${it.key}")
                }
    }
}