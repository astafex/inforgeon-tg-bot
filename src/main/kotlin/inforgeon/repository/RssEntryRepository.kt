package inforgeon.repository

import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssTopicName
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*


interface RssEntryRepository : JpaRepository<RssEntry, Long> {

    fun findAllByIdIn(ids : List<Long>) : List<RssEntry>

    fun findFirstByTopicOrderByIdDesc(topic: RssTopicName) : Optional<RssEntry>

    /** Не работает в jpql */
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

    fun findAllByTagsIn(tags: List<String>) :  List<RssEntry>
    fun findFirstByTopicAndIdNotInOrderByIdDesc(topic: RssTopicName, ids: List<Long>) : Optional<RssEntry>
    fun findFirstByTopicAndIdNotInAndIdLessThanOrderByIdDesc(topic: RssTopicName, ids: List<Long>, id: Long) : Optional<RssEntry>


}