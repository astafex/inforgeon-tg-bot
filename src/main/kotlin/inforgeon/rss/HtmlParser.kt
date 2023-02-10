package inforgeon.inforgeon.rss

import java.io.IOException

/**
 * Класс-парсер текста из веб-страниц
 */
interface HtmlParser {

    /**
     * Метод скачивает посредством http(s) запроса html-код страницы и парсит body
     * @param url - адрес веб-страницы
     * @return parsedText - текст, извлеченный из body html-страницы
     * @throws IOException
     */
    fun parseHtml(url: String): String
}