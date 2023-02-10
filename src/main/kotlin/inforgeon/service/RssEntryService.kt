package inforgeon.inforgeon.service

import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssTopicName

/**
 * Сервис для работы с источниками
 */
interface RssEntryService {

    fun get(id: Long) : RssEntry

    /**
     * Сохранить все
     */
    fun saveAll(rssEntries : Collection<RssEntry>)

    /**
     * Отсеять повторы
     */
    fun distinct(rssEntries : Collection<RssEntry>) : Set<RssEntry>

    /**
     * Отдать самую последнюю новость по теме (исключая дизлайкнутые по тэгам)
     */
    fun getNewest(topic : RssTopicName, stopTags : Collection<String>?) : RssEntry

    /**
     * Отдать следующую за текущей новость по теме (исключая дизлайкнутые по тэгам)
     */
    fun getNext(topic : RssTopicName, rssEntryId: Long, stopTags: Collection<String>?) : RssEntry
}