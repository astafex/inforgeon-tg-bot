package inforgeon.inforgeon.service

import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.entity.UserSettings

/**
 * Контракт по взаимодействию бота с бэкендом. Всю интеграцию с ботом осуществлять только через него
 */
interface BotApiService {

    /**
     * Получить данные пользователя
     */
    fun getUserSettings(userId: Long) : UserSettings?

    fun saveUserSettings(userSettings: UserSettings): UserSettings

    /**
     * Получить самую новую новость по выбранной теме
     */
    fun getNewestRssEntry(userId : Long, topicName: RssTopicName) : RssEntry

    /**
     * Получить следующую, более раннюю новость по той же теме по id текущей
     */
    fun getNextRssEntry(userId : Long, topicName: RssTopicName, rssEntryId : Long) : RssEntry

    /**
     * Дизлайкнуть новость по id (минусятся тэги, подобные новости будут меньше показываться)
     */
    fun dislikeRssEntry(userId : Long, topicName: RssTopicName, rssEntryId : Long)

    /**
     * Дизлайкнуть ниже порога определенный тэг (новость с таким тэгом, если он есть, не будет показана)
     */
    fun filterTag(userId : Long, topicName: RssTopicName, filteredTag : String)

    /**
     * Сбросить все дизлайки по выбранной теме
     */
    fun resetAllDislikes(userId : Long, topicName: RssTopicName)
}