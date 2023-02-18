package inforgeon.inforgeon.service

import inforgeon.inforgeon.entity.UserSettings

/**
 * Сервис для работы с настройками
 */
interface UserSettingsService {

    fun get(userId : Long) : UserSettings?

    fun initializeUser(userSettings : UserSettings) : UserSettings

    fun save(settings: UserSettings) : UserSettings

//    fun resetAllDislikes(username : String, topicName: RssTopicName)
}