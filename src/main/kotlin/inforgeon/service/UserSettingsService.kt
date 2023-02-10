package inforgeon.inforgeon.service

import inforgeon.inforgeon.entity.UserSettings

/**
 * Сервис для работы с настройками
 */
interface UserSettingsService {

    fun get(username : String) : UserSettings

    fun initializeUser(username : String) : UserSettings

    fun save(settings: UserSettings) : UserSettings

//    fun resetAllDislikes(username : String, topicName: RssTopicName)
}