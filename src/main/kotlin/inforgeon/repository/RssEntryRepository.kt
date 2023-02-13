package inforgeon.repository

import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssTopicName
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface RssEntryRepository : JpaRepository<RssEntry, Long> {

    /**
     * Поиск записей по id
     * @param ids список идентификаторов записей
     */
    fun findAllByIdIn(ids : List<Long>) : List<RssEntry>

    /**
     * Поиск первой записи по заданному топику (с сортировкой по убыванию)
     * @param topic топик для фильтрации
     */
    fun findFirstByTopicOrderByIdDesc(topic: RssTopicName) : Optional<RssEntry>

    /**
     * Поиск первой записи по топику и тегу без дизлайков
     * @param topic топик для фильтрации
     * @param tags список тегов
     * Не работает в jpql */
    @Query(value = """
        select * from bot.rss_entry 
        where topic = :topic and id not in 
        (
          select distinct e.id from bot.rss_entry as e
          left join bot.tags as t on e.id = t.entry_id
          where t.tag in (:tags)
        )
        order by id desc limit 1
    """, nativeQuery = true)
    fun findFirstWithoutDisliked(@Param("topic") topic: RssTopicName, @Param("tags") tags: List<String>) : Optional<RssEntry>

    /**
     * Поиск первой записи по указанному топику и id???
     * @param topic топик для фильтрации
     * @param id id записи
     */
    fun findFirstByTopicAndIdLessThanOrderByIdDesc(topic: RssTopicName, id: Long) : Optional<RssEntry>

    /** Не работает в jpql */
    @Query(value = """
        select * from bot.rss_entry
        where topic = :topic and id < :id and id not in
        (
            select distinct e.id from bot.rss_entry as e
            left join bot.tags as t on e.id = t.entry_id
            where t.tag in (:tags)
        )
        order by id desc limit 1
    """, nativeQuery = true)
    fun findNextWithoutDisliked(@Param("topic") topic: RssTopicName,
                                @Param("id") id: Long,
                                @Param("tags") tags: List<String>) : Optional<RssEntry>

    /**
     * Поиск записей по тегам
     * @param tags список тегов
     */
    fun findAllByTagsIn(tags: List<String>) :  List<RssEntry>

    /**
     * Поиск записей по топику и id???
     * @param topic топик для фильтрации
     * @param ids список идентификаторов записей
     */
    fun findFirstByTopicAndIdNotInOrderByIdDesc(topic: RssTopicName, ids: List<Long>) : Optional<RssEntry>

    /**
     * Поиск записей по топику и id???
     * @param topic топик для фильтрации
     * @param ids список идентификаторов записей
     * @param id id записи
     */
    fun findFirstByTopicAndIdNotInAndIdLessThanOrderByIdDesc(topic: RssTopicName, ids: List<Long>, id: Long) : Optional<RssEntry>
}