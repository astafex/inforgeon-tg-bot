package inforgeon.inforgeon.rss.impl

import inforgeon.inforgeon.constant.RssJavaSubtopicName
import inforgeon.inforgeon.constant.RssKotlinSubtopicName
import inforgeon.inforgeon.constant.RssNewsSubtopicName
import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.repository.RssEntryRepository
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("rss")
class CategorizerIml (
    private val htmlParser: JsoupHtmlParser,
    private val rssParser: HabrRssParser,
    private val rssEntryRepository: RssEntryRepository
) {
    var javaSubtopics: Map<RssJavaSubtopicName, List<String>>? = null
    var kotlinSubtopics: Map<RssKotlinSubtopicName, List<String>>? = null
    var newsSubtopics: Map<RssNewsSubtopicName, List<String>>? = null
    var habrUrl: Map<RssTopicName, String>? = null


    fun rssCategorize(topicName: RssTopicName) {
        val feed = rssParser.getFeed(habrUrl?.get(topicName)!!)
        val rssEntries = rssParser.parseRssContent(feed, topicName)
        rssEntryRepository.saveAll(rssEntries)

        rssEntries.forEach {e ->
            val parsedHtml = htmlParser.parseHtml(e.url)


        }
    }
}