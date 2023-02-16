package inforgeon.rss.impl

import inforgeon.inforgeon.rss.impl.JsoupHtmlParser
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class JsoupHtmlParserTest {

    @Test
    fun parseHtmlTest() {
        mockkStatic(Jsoup::class)
        val connection = mockk<Connection>()
        val doc = mockk<Document>()
        every {
            Jsoup.connect(any())
        } returns connection
        every {
            connection.userAgent(any())
        } returns connection
        every {
            connection.referrer(any())
        } returns connection
        every {
            connection.get()
        } returns doc
        val elements = listOf(
            mockk<Element>().apply { every { text() } returns "element"  },
            mockk<Element>().apply { every { text() } returns "element2"  }
        )
        every {
            doc.select("body")
        } returns Elements(elements)
        val jsoupHtmlParser = JsoupHtmlParser()

        val result = jsoupHtmlParser.parseHtml("url")

        Assertions.assertEquals("elementelement2", result)
    }

}