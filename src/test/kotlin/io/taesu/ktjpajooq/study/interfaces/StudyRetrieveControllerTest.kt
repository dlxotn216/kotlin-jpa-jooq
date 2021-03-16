package io.taesu.ktjpajooq.study.interfaces

import io.taesu.ktjpajooq.base.exception.resourceNotFound
import io.taesu.ktjpajooq.study.application.StudyRetrieveService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

/**
 * Created by itaesu on 2021/03/16.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@ExtendWith(SpringExtension::class)
@WebMvcTest(StudyRetrieveController::class)
internal class StudyRetrieveControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var studyRetrieveService: StudyRetrieveService

    @Test
    fun `should success to retrieve study`() {
        // given
        val studyKey = 1L
        val response = StudyRetrieveResponse(key = studyKey, id = "MOCK_STUDY", name = "Mocked study", deleted = false)
        `when`(studyRetrieveService.retrieve(studyKey)).thenReturn(response)

        // when
        val request = MockMvcRequestBuilders.get("/api/v1/studies/$studyKey")
                .accept(MediaType.APPLICATION_JSON)

        // then
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.key").value(studyKey))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.id").value("MOCK_STUDY"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.name").value("Mocked study"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.deleted").value(false))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
    }

    @Test
    fun `should fail to retrieve not exists study`() {
        // given
        val studyKey = 1L
        `when`(studyRetrieveService.retrieve(studyKey)).thenThrow(resourceNotFound())

        // when
        val request = MockMvcRequestBuilders.get("/api/v1/studies/$studyKey")
                .accept(MediaType.APPLICATION_JSON)

        // then
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("fail"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error.errorCode").value("RESOURCE_NOT_FOUND"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
    }
}