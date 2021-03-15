package io.taesu.ktjpajooq.study.interfaces

import io.taesu.ktjpajooq.study.application.StudyCreateService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
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
@WebMvcTest(StudyCreateController::class)
internal class StudyCreateControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var studyCreateService: StudyCreateService

    @Test
    fun shouldSuccessToCreateStudy() {
        // given
        val id = "test_proto"
        val name = "study creation test"
        val studyCreateRequest = StudyCreateRequest(
                id = id, name = name, studyUsers = listOf(1, 2)
        )
        `when`(studyCreateService.create(studyCreateRequest)).thenReturn(1L)

        val requestJson = """
            {
                "id": "$id",
                "name": "$name",
                "studyUsers": [1, 2]
            }
        """.trimIndent()

        //when
        val request = MockMvcRequestBuilders.post("/api/v1/studies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON)

        //then
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.studyKey").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
    }
}