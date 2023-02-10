package inforgeon.inforgeon.service.impl

import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.entity.UserSettings
import inforgeon.inforgeon.service.BotApiService
import inforgeon.inforgeon.service.RssEntryService
import inforgeon.inforgeon.service.UserSettingsService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class BotApiServiceImpl(
    private val userSettingsService : UserSettingsService,
    private val rssEntryService: RssEntryService
) : BotApiService {

    @Value("\${rss.dislikes.threshold}")
    val threshold: Int? = null

    override fun getNewestRssEntry(username: String, topicName: RssTopicName) : RssEntry {
        val settings = userSettingsService.get(username)
        val stopTags = getAllStopTags(settings, topicName)
        return rssEntryService.getNewest(topicName, stopTags)
    }

    override fun getNextRssEntry(username: String, topicName: RssTopicName, rssEntryId: Long) : RssEntry{
        val settings = userSettingsService.get(username)
        val stopTags = getAllStopTags(settings, topicName)
        return rssEntryService.getNext(topicName, rssEntryId, stopTags)
    }

    override fun dislikeRssEntry(username: String, topicName: RssTopicName, rssEntryId: Long) {
        val settings = userSettingsService.get(username)
        val rssEntry = rssEntryService.get(rssEntryId)
        // todo перенести в сервис настроек
        val allTags = settings.dislikedTags
            .map { Pair(it.topic, it.tag) }
            .filter { it.first == topicName }.map {it.second}.toSet()

        rssEntry.tags.forEach { entryTag ->
            if (allTags.contains(entryTag)) {
                settings.dislikedTags
                    .filter { dislikesCounter -> dislikesCounter.topic == topicName && dislikesCounter.tag == entryTag }
                    .forEach { filteredDislikesCounter -> filteredDislikesCounter.count++ }
            }
        }
        userSettingsService.save(settings)
    }

    override fun filterTag(username: String, topicName: RssTopicName, filteredTag: String) {
        val settings = userSettingsService.get(username)
        // todo перенести в сервис настроек
        settings.dislikedTags
            .filter { dislikesCounter -> dislikesCounter.topic == topicName && dislikesCounter.tag == filteredTag }
            .forEach { filteredDislikesCounter -> filteredDislikesCounter.count = threshold!! }
        userSettingsService.save(settings)
    }

    override fun resetAllDislikes(username: String, topicName: RssTopicName) {
        val settings = userSettingsService.get(username)
        // todo перенести в сервис настроек
        settings.dislikedTags
            .filter { dislikesCounter -> dislikesCounter.topic == topicName }
            .forEach { it.count = 0 }
        userSettingsService.save(settings)
    }

    private fun getAllStopTags(settings: UserSettings, topicName: RssTopicName) : Set<String> {
        return settings.dislikedTags.filter { it.count >= threshold!! && it.topic == topicName }.map { it.tag }.toSet()
    }
}