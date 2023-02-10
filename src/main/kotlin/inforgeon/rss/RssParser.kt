package inforgeon.rss

import com.sun.syndication.feed.synd.SyndFeed
import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssTopicName

interface RssParser {
    fun getFeed(url: String) : SyndFeed
    fun parseRssContent(feed: SyndFeed, topicName: RssTopicName) : List<RssEntry>
}