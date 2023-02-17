package inforgeon.rss.impl

import com.sun.syndication.feed.synd.SyndFeed
import inforgeon.entity.RssEntry
import inforgeon.getSyndEntry
import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.rss.impl.HabrRssParser
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class HabrRssParserTest {

    @Test
    fun parseRssContentTest() {

        val sf = mockk<SyndFeed>()
        every { sf.title } returns "sf_title"
        every { sf.description } returns "sf_description"
        val tn = RssTopicName.JAVA

        val entries = listOf<Long>(1, 2).map { i ->
            RssEntry(10 + i, "title$i", "author$i", "habr.com/ru/post/$i/sdfsdf", "desc$i", tn)
        }

        entries.map { e ->
            getSyndEntry(e.title, e.author, e.url, e.description)
        }.also { ent ->
            every { sf.entries } returns ent
        }

        val rssEntryList = HabrRssParser().parseRssContent(sf, tn)

        Assertions.assertEquals(entries, rssEntryList)

    }

}