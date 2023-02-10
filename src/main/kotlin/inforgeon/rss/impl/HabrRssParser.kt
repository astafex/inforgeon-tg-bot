package inforgeon.inforgeon.rss.impl

import com.sun.syndication.feed.synd.SyndEntry
import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader
import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.utils.getIdFromHabrUrl
import inforgeon.rss.RssParser
import org.springframework.stereotype.Component
import java.net.URL

@Component
class HabrRssParser : RssParser {

    override fun getFeed(url: String) :SyndFeed {
       return SyndFeedInput().build(XmlReader(URL(url)))
    }

    override fun parseRssContent(feed: SyndFeed, topicName: RssTopicName) : List<RssEntry> {
        printRssFeed(feed)
        return feed.entries.map { e ->
            val entry = e as SyndEntry
            // определим id статьи на хабре. Этот же id и станет нашим id
            val id = getIdFromHabrUrl(entry.uri) ?: throw java.lang.IllegalArgumentException("Id в url не найден!")
            RssEntry(
                id = id,
                title = entry.title,
                author = entry.author,
                url = entry.uri,
                description = entry.description.value,
                topic = topicName
            )
        }.toList()
    }

    private fun printRssFeed(feed: SyndFeed) {
        println("Title: ${feed.title}")
        println("Description: ${feed.description}")
        feed.entries.forEach { entry ->
            val syndEntry = entry as SyndEntry
            println("Title ${syndEntry.title}")
            println("Author ${syndEntry.author}")
            println("Url ${syndEntry.uri}")
            println("Description ${syndEntry.description.value}")
            println()
        }
    }

}


