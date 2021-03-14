package io.taesu.ktjpajooq

import io.taesu.ktjpajooq.study.domain.Study
import io.taesu.ktjpajooq.study.domain.StudyRepository
import io.taesu.ktjpajooq.user.domain.User
import io.taesu.ktjpajooq.user.domain.UserRepository
import io.taesu.ktjpajooq.user.infra.UserQuery
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@SpringBootApplication
class KtjpajooqApplication

fun main(args: Array<String>) {
    runApplication<KtjpajooqApplication>(*args)
}

@Component
class Runner(
        val userRepository: UserRepository,
        val studyRepository: StudyRepository,
        val userQuery: UserQuery
) : ApplicationRunner {
    @Transactional
    override fun run(args: ApplicationArguments?) {
        // val user1 = userRepository.save(User(
        //         id = "taesu1",
        //         name = "Lee1",
        //         email = "taesu1@crscube.co.kr"))
        // val user2 = userRepository.save(User(
        //         id = "taesu2",
        //         name = "Lee2",
        //         email = "taesu2@crscube.co.kr"))
        //
        // val study = Study(id = "std1", name = "test1 study")
        // study.addUser(user1)
        // study.addUser(user1)
        // study.addUser(user2)
        // study.addUser(user2)
        //
        // studyRepository.save(study)
    }
}