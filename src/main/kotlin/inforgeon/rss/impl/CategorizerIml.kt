package inforgeon.inforgeon.rss.impl

import inforgeon.inforgeon.constant.RssSuptopicName
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
    var subtopics: Map<RssTopicName, Map<RssSuptopicName, List<String>>>? = null
//    var javaSubtopics: Map<RssJavaSubtopicName, List<String>>? = null
//    var kotlinSubtopics: Map<RssKotlinSubtopicName, List<String>>? = null
//    var newsSubtopics: Map<RssNewsSubtopicName, List<String>>? = null
    var habrUrl: Map<RssTopicName, String>? = null


    fun rssCategorize(topicName: RssTopicName) {
        val feed = rssParser.getFeed(habrUrl?.get(topicName)!!)
        val rssEntries = rssParser.parseRssContent(feed, topicName)

        rssEntries.forEach { entry ->
            val parsedHtml = htmlParser.parseHtml(entry.url)
//            var register = TreeMap<List<String>, RssSuptopicName>(Comparator.comparing { (t1, t2) -> t1.length > t2.length })
            val register = HashMap<RssSuptopicName, List<String>>()

            subtopics?.get(topicName)!!.forEach { subtopic ->
                subtopic.value.forEach { tag ->
                    if (parsedHtml.contains(tag)) {
                        var subtopicTags = register[subtopic.key]
                        subtopicTags = subtopicTags?.plus(tag) ?: listOf(tag)
                        register.put(subtopic.key, subtopicTags)
                    }
                }
            }
            val calcSubtopic = register.map { Pair(it.value, it.key) }.maxByOrNull { it.first.size }!!
            entry.subtopic = calcSubtopic.second
            entry.tags = calcSubtopic.first
        }
        rssEntryRepository.saveAll(rssEntries)
    }
}