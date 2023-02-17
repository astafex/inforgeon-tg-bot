package inforgeon.rss.impl

import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssSubtopicName
import inforgeon.inforgeon.constant.RssSubtopicName.*
import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.constant.RssTopicName.*
import inforgeon.inforgeon.rss.HtmlParser
import inforgeon.inforgeon.rss.impl.CategorizerImpl
import inforgeon.inforgeon.service.RssEntryService
import inforgeon.rss.RssParser
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CategorizerImplTest {

    @Test
    fun rssCategorizeTest() {

        val subtopics = mapOf<RssTopicName, MutableMap<RssSubtopicName, List<String>>>(
            Pair(JAVA, mutableMapOf()), Pair(KOTLIN, mutableMapOf()), Pair(NEWS, mutableMapOf())
        )

        val habrUrl: MutableMap<RssTopicName, String> = mutableMapOf()
        val entries = mutableMapOf<RssTopicName, MutableSet<RssEntry>>()
        val entriesForCheck = mutableMapOf<RssTopicName, MutableSet<RssEntry>>()
        val rssParser = mockk<RssParser>()
        val htmlParser = mockk<HtmlParser>()
        val rssEntryService = mockk<RssEntryService>()

        subtopics[JAVA]?.put(CORE, listOf("runtime", "reflection"))
        subtopics[JAVA]?.put(OOP, listOf("наследовани", "solid"))

        subtopics[KOTLIN]?.put(CORE, listOf("val", "multiplatform", "any"))
        subtopics[KOTLIN]?.put(COLLECTIONS, listOf("list", "map", "set", "queue"))

        subtopics[NEWS]?.put(BACKEND, listOf("микросервис", "архитектур", "kafka", "брокер"))
        subtopics[NEWS]?.put(DEVOPS, listOf("инфраструктур", "jenkins", "ansible", "terraform", "kuber", "kubernetes"))

        subtopics.forEach { (tn, subTopicToTabs) ->
            var i = 0L
            subTopicToTabs.forEach { subTopicEntry ->
                val id = tn.ordinal * 10 + i++
                val title = "title$id"
                val url = "url$id"
                val author = "author$id"
                val desc = "description$id"
                habrUrl[tn] = url
                entries.computeIfAbsent(tn) { mutableSetOf() }.add(
                    RssEntry(id, title, author, url, desc, tn)
                )
                entriesForCheck.computeIfAbsent(tn) { mutableSetOf() }.add(
                    RssEntry(id, title, author, url, desc, tn, subTopicEntry.key, subTopicEntry.value)
                )
                every { rssParser.getFeed(any()) } returns mockk()
                every { rssParser.parseRssContent(any(), tn) } returns entries[tn]!!.toList()
                every { rssEntryService.distinct(any()) } answers {
                    (it.invocation.args[0] as List<RssEntry>).toSet()
                }
                every { htmlParser.parseHtml(url) } returns subTopicEntry.value.joinToString(separator = "")
            }
        }

        val categorizerImpl = CategorizerImpl(htmlParser, rssParser, rssEntryService)
        categorizerImpl.subtopics = subtopics
        categorizerImpl.habrUrl = habrUrl

        subtopics.forEach { (tn, _) ->

            var newRssEntries: MutableSet<RssEntry>? = null

            every {
                rssEntryService.saveAll(any())
            } answers {
                newRssEntries = it.invocation.args[0] as MutableSet<RssEntry>
            }

            categorizerImpl.rssCategorize(tn)

            Assertions.assertEquals(entriesForCheck[tn], newRssEntries)

        }



    }

}