package io.taesu.ktjpajooq.study.interfaces

import io.taesu.ktjpajooq.base.exception.InvalidRequestException
import io.taesu.ktjpajooq.study.application.StudyCreateService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
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
@ExtendWith(SpringExtension::class)
@WebMvcTest(StudyCreateController::class)
internal class StudyCreateControllerTest {

    @MockBean
    lateinit var studyCreateService: StudyCreateService

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun shouldSuccessToCreateStudy() {
        // given
        val id = "test_proto"
        val name = "study creation test"
        `when`(studyCreateService.create(any(StudyCreateRequest::class.java))).thenReturn(1L)

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

    @Test
    fun shouldFailToCreateStudy() {
        // given
        val id = "test_proto"
        val name = "study creation test"
        `when`(studyCreateService.create(any(StudyCreateRequest::class.java)))
                .thenThrow(InvalidRequestException("DUPLICATED_ID", "duplicated study id", HttpStatus.CONFLICT))

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
                .andExpect(MockMvcResultMatchers.status().isConflict)
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("fail"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error.errorCode").value("DUPLICATED_ID"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
    }

    private fun <T> any(clazz: Class<T>): T {
        Mockito.any(clazz)
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T
}