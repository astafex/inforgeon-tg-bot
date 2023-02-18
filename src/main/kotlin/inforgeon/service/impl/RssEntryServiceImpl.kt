package inforgeon.inforgeon.service.impl

import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.service.RssEntryService
import inforgeon.repository.RssEntryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RssEntryServiceImpl(private val repository: RssEntryRepository) : RssEntryService {

    @Transactional(readOnly = true)
    @Throws(java.util.NoSuchElementException::class)
    override fun get(id: Long) : RssEntry {
        return repository.findById(id).orElseThrow()
    }

    @Transactional
    override fun saveAll(rssEntries : Collection<RssEntry>) {
        repository.saveAll(rssEntries)
    }

    @Transactional(readOnly = true)
    override fun distinct(rssEntries: Collection<RssEntry>): Set<RssEntry> {
        val saved = repository.findAllByIdIn(rssEntries.map { it.id }).toSet()
        return rssEntries.filterNot { saved.contains(it) }.toSet()
    }

    @Transactional(readOnly = true)
    @Throws(NoSuchElementException::class)
    override fun getNewest(topic: RssTopicName, stopTags: Collection<String>?): RssEntry {
        return if (stopTags == null || stopTags.isEmpty()) {
            repository.findFirstByTopicOrderByIdDesc(topic).orElseThrow()
        } else {
    //            repository.findFirstWithoutDisliked(topic, ArrayList(stopTags)).orElseThrow()
            val filteredIds = repository.findAllByTagsIn(stopTags.toList()).map {it.id}.distinct()
            repository.findFirstByTopicAndIdNotInOrderByIdDesc(topic, filteredIds).orElseThrow()
        }
    }

    @Transactional(readOnly = true)
    @Throws(NoSuchElementException::class)
    override fun getNext(topic: RssTopicName, rssEntryId: Long, stopTags: Collection<String>?): RssEntry {
        return if (stopTags == null || stopTags.isEmpty()) {
            repository.findFirstByTopicAndIdLessThanOrderByIdDesc(topic, rssEntryId).orElse(this.getNewest(topic, stopTags))
        } else {
    //            repository.findNextWithoutDisliked(topic, rssEntryId, stopTags.toList()).orElseThrow()
            val filteredIds = repository.findAllByTagsIn(stopTags.toList()).map {it.id}.distinct()
            repository
                .findFirstByTopicAndIdNotInAndIdLessThanOrderByIdDesc(topic, filteredIds, rssEntryId)
                .orElse(this.getNewest(topic, stopTags))
        }
    }
}