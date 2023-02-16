package inforgeon

import com.sun.syndication.feed.synd.SyndFeed
import inforgeon.inforgeon.constant.RssTopicName.JAVA
import inforgeon.inforgeon.rss.Categorizer
import inforgeon.inforgeon.rss.impl.HabrRssParser
import inforgeon.inforgeon.rss.impl.JsoupHtmlParser
import inforgeon.inforgeon.service.BotApiService
import inforgeon.inforgeon.service.UserSettingsService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [ App::class ])
@ActiveProfiles("test")
@Import(IntegrationTest.TestConfig::class)
@Sql("classpath:/sql/truncate.sql")
class IntegrationTest {

    @Configuration
    class TestConfig {

        @Bean
        @Primary
        fun testJsoupHtmlParser() = TestJsoupHtmlParser()

        @Bean
        @Primary
        fun restHabrRssParser() = TestHabrRssParser()

    }

    class TestJsoupHtmlParser : JsoupHtmlParser() {

        var urlBody: MutableMap<String, String> = mutableMapOf()

        override fun parseHtml(url: String): String {
            return urlBody[url] ?: throw IllegalStateException("Для url=$url не найден ответ")
        }
    }

    class TestHabrRssParser: HabrRssParser() {

        var feed : MutableMap<String, SyndFeed> = mutableMapOf()

        override fun getFeed(url: String): SyndFeed {
            return feed[url] ?: throw IllegalStateException("Для url=$url не найдены записи RSS ленты")
        }

    }

    @Autowired
    private lateinit var testJsoupHtmlParser: TestJsoupHtmlParser

    @Autowired
    private lateinit var restHabrRssParser: TestHabrRssParser

    @Value("\${rss.habrUrl.JAVA}")
    private lateinit var urlJava: String

    @Autowired
    private lateinit var categorizer: Categorizer

    @Autowired
    private lateinit var botApiService: BotApiService

    @Autowired
    private lateinit var userSettingsService: UserSettingsService

    @Test
    @Transactional
    fun test() {
        restHabrRssParser.feed[urlJava] = getSyndFeed(mutableListOf(getSyndEntry(title = "title1", url = "habr.com/ru/post/1/sdfsdf")))
        testJsoupHtmlParser.urlBody["habr.com/ru/post/1/sdfsdf"] = "offcetdatetimejodaoptional"
        categorizer.rssCategorize(JAVA)
        userSettingsService.initializeUser("user")
        var entry = botApiService.getNewestRssEntry("user", JAVA)
        println(rssEntryToString(entry))
    }

}