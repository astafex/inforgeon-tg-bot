package inforgeon.inforgeon.rss.impl

import inforgeon.inforgeon.rss.HtmlParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class JsoupHtmlParser : HtmlParser {

    var userAgent = "Chrome/4.0.249.0 Safari/532.5"
    var referrer = "http://www.google.com"

    @Throws(IOException::class)
    override fun parseHtml(url: String): String {
        //получить содержимое веб-страницы по адресу url
        val doc = Jsoup.connect(url)
            .userAgent(userAgent)
            .referrer(referrer)
            .get()

        //выбрать все элементы из body
        val elements = doc.select("body")
        //спарсить текст и сохранить в билдер
        val parsedText = StringBuilder()
        elements.stream().forEach { element: Element ->
            parsedText.append(
                element.text()
            )
        }
        return parsedText.toString().lowercase().trim()
    }
}