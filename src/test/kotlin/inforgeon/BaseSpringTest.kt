package inforgeon

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
internal class BaseSpringTest {

//    @Autowired
//    private lateinit var categorizer: Categorizer
//
//    @Test
//    fun rssTest() {
//        val rssParser = HabrRssParser()
//        val rssEntries =
//            rssParser.parseRssContent(rssParser.getFeed("https://habr.com/ru/rss/all/all/?fl=ru&limit=100"), RssTopicName.NEWS)
//
//        val size = rssEntries.size
//
//        println("OK")
//    }
//
//    @Test
//    fun categorizerTest() {
////        categorizerImpl.rssCategorize(RssTopicName.KOTLIN )
//        RssTopicName.values().forEach { topic ->
//            categorizer.rssCategorize(topic)
//        }
//        println("OK")
//    }

    //TODO тесты
}