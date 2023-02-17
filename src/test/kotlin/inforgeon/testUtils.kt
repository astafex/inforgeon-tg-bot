package inforgeon

import com.sun.syndication.feed.synd.SyndContent
import com.sun.syndication.feed.synd.SyndEntry
import com.sun.syndication.feed.synd.SyndFeed
import inforgeon.entity.RssEntry
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

