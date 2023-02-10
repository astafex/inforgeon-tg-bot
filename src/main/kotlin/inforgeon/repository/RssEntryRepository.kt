package inforgeon.repository

import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssTopicName
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface RssEntryRepository : JpaRepository<RssEntry, Long> {

    fun findAllByIdIn(ids : List<Long>) : List<RssEntry>

    fun findFirstByTopicOrderByIdDesc(topic: RssTopicName) : Optional<RssEntry>
    fun findFirstByTopicAndTagsNotInOrderByIdDesc(topic: RssTopicName, tags: List<String>) : Optional<RssEntry>

    fun findFirstByTopicAndIdLessThanEqualOrderByIdDesc(topic: RssTopicName, id: Long) : Optional<RssEntry>
    fun findFirstByTopicAndIdLessThanEqualAndTagsNotInOrderByIdDesc(topic: RssTopicName, id: Long, tags: List<String>) : Optional<RssEntry>
}