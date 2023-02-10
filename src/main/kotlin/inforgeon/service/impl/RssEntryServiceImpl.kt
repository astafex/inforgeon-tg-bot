package inforgeon.inforgeon.service.impl

import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.service.RssEntryService
import inforgeon.repository.RssEntryRepository
import org.springframework.stereotype.Service

@Service
class RssEntryServiceImpl(private val repository: RssEntryRepository) : RssEntryService {

    @Throws(java.util.NoSuchElementException::class)
    override fun get(id: Long) : RssEntry {
        return repository.findById(id).orElseThrow()
    }

    override fun saveAll(rssEntries : Collection<RssEntry>) {
        repository.saveAll(rssEntries)
    }

    override fun distinct(rssEntries: Collection<RssEntry>): Set<RssEntry> {
        val saved = repository.findAllByIdIn(rssEntries.map { it.id }).toSet()
        return rssEntries.filterNot { saved.contains(it) }.toSet()
    }

    @Throws(NoSuchElementException::class)
    override fun getNewest(topic: RssTopicName, stopTags: Collection<String>?): RssEntry {
        return if (stopTags == null || stopTags.isEmpty()) {
            repository.findFirstByTopicOrderByIdDesc(topic).orElseThrow()
        } else {
            repository.findFirstByTopicAndTagsNotInOrderByIdDesc(topic, stopTags.toList()).orElseThrow()
        }
    }

    @Throws(NoSuchElementException::class)
    override fun getNext(topic: RssTopicName, rssEntryId: Long, stopTags: Collection<String>?): RssEntry {
        return if (stopTags == null || stopTags.isEmpty()) {
            repository.findFirstByTopicAndIdLessThanEqualOrderByIdDesc(topic, rssEntryId).orElseThrow()
        } else {
            repository.findFirstByTopicAndIdLessThanEqualAndTagsNotInOrderByIdDesc(topic, rssEntryId, stopTags.toList())
                .orElseThrow()
        }
    }




}