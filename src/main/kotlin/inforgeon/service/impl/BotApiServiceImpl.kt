package inforgeon.inforgeon.service.impl

import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.entity.DislikedTagCounter
import inforgeon.inforgeon.entity.UserSettings
import inforgeon.inforgeon.service.BotApiService
import inforgeon.inforgeon.service.RssEntryService
import inforgeon.inforgeon.service.UserSettingsService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BotApiServiceImpl(
    private val userSettingsService: UserSettingsService,
    private val rssEntryService: RssEntryService
) : BotApiService {

    @Value("\${rss.dislikes.threshold}")
    val threshold: Int? = null

    @Transactional
    override fun getUserSettings(userId: Long): UserSettings? {
        return userSettingsService.get(userId) ?: userSettingsService.initializeUser(UserSettings(userId))
    }

    @Transactional
    override fun saveUserSettings(userSettings: UserSettings): UserSettings {
        return userSettingsService.initializeUser(userSettings)
    }

    @Transactional(readOnly = true)
    override fun getNewestRssEntry(userId: Long, topicName: RssTopicName): RssEntry {
        val settings = userSettingsService.get(userId)
        val stopTags = getAllStopTags(settings!!, topicName)
        return rssEntryService.getNewest(topicName, stopTags)
    }

    @Transactional(readOnly = true)
    override fun getNextRssEntry(userId: Long, topicName: RssTopicName, rssEntryId: Long): RssEntry {
        val settings = userSettingsService.get(userId)
        val stopTags = getAllStopTags(settings!!, topicName)
        return rssEntryService.getNext(topicName, rssEntryId, stopTags)
    }

    @Transactional
    override fun dislikeRssEntry(userId: Long, topicName: RssTopicName, rssEntryId: Long) {
        var settings = userSettingsService.get(userId)
        val rssEntry = rssEntryService.get(rssEntryId)
        // выделить все дизлайкнутые пользователем тэги
        val allDislikedTags = getAllUserDislikedTags(settings!!, topicName)

        // заминусить каждый тэг
        rssEntry.tags.forEach { entryTag ->
            // если тэг уже есть, то прибавить дизлайк
            if (allDislikedTags.contains(entryTag)) {
                settings.dislikedTags
                    .filter { dislikesCounter -> dislikesCounter.topic == topicName && dislikesCounter.tag == entryTag }
                    .forEach { filteredDislikesCounter -> filteredDislikesCounter.count++ }
            } // если тэг нет, то создать дизлайк
            else {
                settings.dislikedTags +=
                    DislikedTagCounter(topic = topicName, tag = entryTag, count = 1, settings = settings)
            }
        }

    }

    @Transactional
    override fun filterTag(userId: Long, topicName: RssTopicName, filteredTag: String) {
        val settings = userSettingsService.get(userId)
        // выделить все дизлайкнутые пользователем тэги
        val allDislikedTags = getAllUserDislikedTags(settings!!, topicName)

        // если тэг уже есть, то повысить его до стопа
        if (allDislikedTags.contains(filteredTag)) {
            settings.dislikedTags
                .filter { dislikesCounter -> dislikesCounter.topic == topicName && dislikesCounter.tag == filteredTag }
                .forEach { filteredDislikesCounter -> filteredDislikesCounter.count = threshold!! }
        } // если тэга нет, то создать стоп тэг
        else {
            settings.dislikedTags +=
                DislikedTagCounter(topic = topicName, tag = filteredTag, count = threshold!!, settings = settings)
        }
    }

    @Transactional
    override fun resetAllDislikes(userId: Long, topicName: RssTopicName) {
        val settings = userSettingsService.get(userId)
        settings!!.dislikedTags
            .filter { dislikesCounter -> dislikesCounter.topic == topicName }
            .forEach { it.count = 0 }
    }

    private fun getAllStopTags(settings: UserSettings, topicName: RssTopicName): Set<String> {
        return settings.dislikedTags
            .filter { it.count >= threshold!! && it.topic == topicName }
            .map { it.tag }
            .toSet()
    }

    private fun getAllUserDislikedTags(settings: UserSettings, topicName: RssTopicName): Set<String> {
        return settings.dislikedTags
            .map { Pair(it.topic, it.tag) }
            .filter { it.first == topicName }.map { it.second }.toSet()
    }
}