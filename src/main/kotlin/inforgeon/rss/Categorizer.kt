package inforgeon.inforgeon.rss

import inforgeon.inforgeon.constant.RssTopicName

/**
 * Категоризатор свежей ленты на основе расчета суммарного веса частоты тэгов в статье.
 * Игнорирует уже записанные в БД статьи
 */
interface Categorizer {

    /**
     * Категоризация ленты конкретного топика
     */
    fun rssCategorize(topicName: RssTopicName)
}