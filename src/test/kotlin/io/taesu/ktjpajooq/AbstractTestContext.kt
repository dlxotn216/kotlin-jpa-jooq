package io.taesu.ktjpajooq

import io.taesu.ktjpajooq.base.interfaces.ExceptionHandlerAdvice
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.web.filter.CharacterEncodingFilter
import java.nio.charset.StandardCharsets

/**
 * Created by itaesu on 2021/03/14.
 *
 * @author Lee Tae Su
 * @version TBD
 * @since TBD
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
internal abstract class AbstractTestContext {
    protected var mockMvc: MockMvc? = null

    protected abstract fun controller(): Any?

    @BeforeEach
    private fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller())
                .addFilter<StandaloneMockMvcBuilder>(CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
                .setControllerAdvice(ExceptionHandlerAdvice::class.java)
                .alwaysDo<StandaloneMockMvcBuilder>(MockMvcResultHandlers.print())
                .build()
    }
}