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
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles


/**
 * Абстрактный класс интеграционных тестов
 */
@SpringBootTest(classes = [App::class])
@Import(AbstractIntegrationTest.TestConfig::class)
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {

    @Configuration
    class TestConfig {

        @Bean
        @Primary
        fun testJsoupHtmlParser() = TestJsoupHtmlParser

        @Bean
        @Primary
        fun restHabrRssParser() = TestHabrRssParser

        /**
         * Пост процессор, не позволяющий создасться бину InforgeonBot
         */
        @Bean
        fun disableInforgeonBot(): BeanFactoryPostProcessor {
            return BeanFactoryPostProcessor {
                val inforgeonBotBeanName = "inforgeonBot"
                if (it.containsBeanDefinition(inforgeonBotBeanName)) {
                    (it as BeanDefinitionRegistry).removeBeanDefinition(inforgeonBotBeanName)
                }
            }
        }

    }

    @Autowired
    protected lateinit var categorizer: Categorizer

    @Autowired
    protected lateinit var botApiService: BotApiService

    @Autowired
    protected lateinit var userSettingsService: UserSettingsService

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setUp() {
        jdbcTemplate.update("delete from bot.tags")
        jdbcTemplate.update("delete from bot.disliked")
        jdbcTemplate.update("delete from bot.user_settings")
        jdbcTemplate.update("delete from bot.rss_entry")
        clearParsersContent()
    }

}