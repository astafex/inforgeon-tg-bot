package inforgeon.inforgeon.service

import inforgeon.entity.RssEntry

/**
 * Сервис для работы с источниками
 */
interface RssEntryService {

    /**
     * Сохранить все
     */
    fun saveAll(rssEntries : Collection<RssEntry>)

    /**
     * Отсеять повторы
     */
    fun distinct(rssEntries : Collection<RssEntry>) : Set<RssEntry>
}