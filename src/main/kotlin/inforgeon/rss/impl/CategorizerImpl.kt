package inforgeon.inforgeon.rss.impl

import inforgeon.inforgeon.constant.RssSubtopicName
import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.rss.Categorizer
import inforgeon.inforgeon.rss.HtmlParser
import inforgeon.inforgeon.service.RssEntryService
import inforgeon.rss.RssParser
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


@Component
@ConfigurationProperties("rss")
class CategorizerImpl (
    private val htmlParser: HtmlParser,
    private val rssParser: RssParser,
    private val rssEntryService: RssEntryService
    ) : Categorizer
{
    var subtopics: Map<RssTopicName, Map<RssSubtopicName, List<String>>>? = null
    var habrUrl: Map<RssTopicName, String>? = null

    override fun rssCategorize(topicName: RssTopicName)  {
        val feed = rssParser.getFeed(habrUrl?.get(topicName)!!)
        val rssEntries = rssParser.parseRssContent(feed, topicName)
        // отсеять повторы
        val newRssEntries = rssEntryService.distinct(rssEntries)

        newRssEntries.forEach { entry ->
            val parsedHtml = htmlParser.parseHtml(entry.url)
            val tagCounter = HashMap<RssSubtopicName, Map<String, Int>>()

            subtopics?.get(topicName)!!.forEach { subtopic ->
                subtopic.value.forEach { tag ->

                    if (parsedHtml.contains(tag)) {
                        val count = tagCount(parsedHtml, tag)
                        if (tagCounter[subtopic.key] == null) {
                            tagCounter[subtopic.key] = hashMapOf(Pair(tag, count))
                        } else {
                            (tagCounter[subtopic.key] as HashMap)[tag] = count
                        }
                    }
                }
            }
            val calcSubtopic = tagCounter
                .map {  Pair(it.value, it.key) }
                .maxByOrNull { it.first.values.sum() }!!
            entry.subtopic = calcSubtopic.second
            entry.tags = calcSubtopic.first.keys.toList()
        }
        rssEntryService.saveAll(newRssEntries)
    }

    private fun tagCount(parsedHtml : String, tag : String) : Int {
        var index = 0
        var count = 0
        while (index != -1) {
            index = parsedHtml.indexOf(tag, index)
            if (index != -1) {
                count++
                index++
            }
        }
        return count
    }
}