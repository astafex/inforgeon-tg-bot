package inforgeon

import com.sun.syndication.feed.synd.SyndContent
import com.sun.syndication.feed.synd.SyndEntry
import com.sun.syndication.feed.synd.SyndFeed
import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.constant.RssTopicName.*
import inforgeon.inforgeon.rss.impl.HabrRssParser
import inforgeon.inforgeon.rss.impl.JsoupHtmlParser
import io.mockk.every
import io.mockk.mockk

fun getSyndEntry(title: String = "title", author: String? = "author", url: String, desc: String? = null): SyndEntry {
    return mockk<SyndEntry>().also { se ->
        every { se.title } returns title
        every { se.author } returns author
        every { se.uri } returns url
        mockk<SyndContent>().also { sc ->
            every { sc.value } returns desc
        }.also { sc ->
            every { se.description } returns sc
        }
    }
}

fun getSyndFeed(entries: MutableList<SyndEntry>) = mockk<SyndFeed>().also { sf ->
    every { sf.entries } returns entries
    every { sf.title } returns "title"
    every { sf.description } returns "description"
}

private val rssUrl = mapOf(
    JAVA to "https://habr.com/ru/rss/hub/java/all/?fl=ru&limit=100",
    KOTLIN to "https://habr.com/ru/rss/hub/kotlin/all/?fl=ru&limit=100",
    NEWS to "https://habr.com/ru/rss/all/all/?fl=ru&limit=100"
)

fun configureParsers(
    topic: RssTopicName,
    parserEntries: List<Pair<String, String>>
) {
    val entries = mutableListOf<SyndEntry>()
    parserEntries.forEachIndexed { i, e ->
        entries.add(getSyndEntry(title = "title$i", url = e.first))
        TestJsoupHtmlParser.urlBody[e.first] = e.second
    }
    TestHabrRssParser.feed[rssUrl[topic]!!] = getSyndFeed(entries)
}

fun checkEntryTags(entry: RssEntry, vararg tags: String) = mutableSetOf<String>().apply { addAll(entry.tags) }.equals(tags.toSet())

object TestJsoupHtmlParser : JsoupHtmlParser() {

    var urlBody: MutableMap<String, String> = mutableMapOf()

    override fun parseHtml(url: String): String {
        return urlBody[url] ?: throw IllegalStateException("Для url=$url не найден ответ")
    }
}

object TestHabrRssParser: HabrRssParser() {

    var feed : MutableMap<String, SyndFeed> = mutableMapOf()

    override fun getFeed(url: String): SyndFeed {
        return feed[url] ?: throw IllegalStateException("Для url=$url не найдены записи RSS ленты")
    }

}

fun clearParsersContent() {
    TestJsoupHtmlParser.urlBody.clear()
    TestHabrRssParser.feed.clear()
}