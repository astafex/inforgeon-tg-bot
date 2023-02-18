package inforgeon.integration

import inforgeon.App
import inforgeon.TestHabrRssParser
import inforgeon.TestJsoupHtmlParser
import inforgeon.clearParsersContent
import inforgeon.inforgeon.rss.Categorizer
import inforgeon.inforgeon.service.BotApiService
import inforgeon.inforgeon.service.UserSettingsService
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql


/**
 * Абстрактный класс интеграционных тестов
 */
@SpringBootTest(classes = [App::class])
@Import(AbstractIntegrationTest.TestConfig::class)
@ActiveProfiles("test")
@Sql("classpath:/sql/truncate.sql")
abstract class AbstractIntegrationTest {

    @Configuration
    class TestConfig {

        @Bean
        @Primary
        fun testJsoupHtmlParser() = TestJsoupHtmlParser

        @Bean
        @Primary
        fun restHabrRssParser() = TestHabrRssParser

    }

    @Autowired
    protected lateinit var categorizer: Categorizer

    @Autowired
    protected lateinit var botApiService: BotApiService

    @Autowired
    protected lateinit var userSettingsService: UserSettingsService

    @BeforeEach
    fun setUp() {
        clearParsersContent()
    }

}