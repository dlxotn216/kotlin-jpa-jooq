package io.taesu.ktjpajooq.study.interfaces

import io.taesu.ktjpajooq.AbstractTestContext
import io.taesu.ktjpajooq.user.domain.User
import io.taesu.ktjpajooq.user.domain.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

/**
 * Created by itaesu on 2021/03/14.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
internal class StudyCreateControllerTest : AbstractTestContext() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var studyCreateController: StudyCreateController

    override fun controller() = studyCreateController

    @Test
    fun shouldSuccessToCreateStudy() {
        // given
        val userKeys = userRepository.saveAll(
                listOf(
                        User(id = "taesu1", email = "taesu1@crscube.co.kr", name = "Lee Tae Su"),
                        User(id = "taesu2", email = "taesu2@crscube.co.kr", name = "Lee Tae Su"))
        ).map { it.key!! }
        val requestJson = """
            {
                "id": "test_proto",
                "name": "study creation test",
                "studyUsers": [${userKeys.joinToString(",")}]
            }
        """.trimIndent()

        //when
        val request = MockMvcRequestBuilders.post("/api/v1/studies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON)

        //then
        mockMvc!!.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.studyKey").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
    }
}