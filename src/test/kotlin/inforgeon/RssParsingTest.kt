package inforgeon

import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.rss.impl.CategorizerImpl
import inforgeon.inforgeon.rss.impl.HabrRssParser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

//@DataJpaTest
@SpringBootTest
@ActiveProfiles("test")
//@AutoConfigureTestDatabase
//    (replace=AutoConfigureTestDatabase.Replace.NONE)
internal class RssParsingTest {

    @Autowired
    private lateinit var categorizerImpl: CategorizerImpl

    @Test
    fun rssTest() {
        val rssParser = HabrRssParser()
        val rssEntries =
            rssParser.parseRssContent(rssParser.getFeed("https://habr.com/ru/rss/all/all/?fl=ru&limit=100"), RssTopicName.NEWS)

        val size = rssEntries.size

        println("OK")
    }

    @Test
    fun categorizerTest() {
        categorizerImpl.rssCategorize(RssTopicName.NEWS )
        println("OK")
    }
}