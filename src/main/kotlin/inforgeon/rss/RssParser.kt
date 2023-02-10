package inforgeon.rss

import com.sun.syndication.feed.synd.SyndFeed
import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssTopicName

/**
 * Парсер RSS ленты
 */
interface RssParser {

    /**
     * Получить ленту по url
     */
    fun getFeed(url: String) : SyndFeed

    /**
     * Получить список источников из ленты
     */
    fun parseRssContent(feed: SyndFeed, topicName: RssTopicName) : List<RssEntry>
}