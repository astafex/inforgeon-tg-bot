package inforgeon.inforgeon.rss


/**
 * Класс-парсер текста из веб-страниц
 */
interface HtmlParser {

    /**
     * Парсинг body html страницы
     */
    fun parseHtml(url: String): String
}