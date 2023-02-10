package inforgeon.inforgeon.service.impl

import inforgeon.entity.RssEntry
import inforgeon.inforgeon.service.RssEntryService
import inforgeon.repository.RssEntryRepository
import org.springframework.stereotype.Service

@Service
class RssEntryServiceImpl(private val repository: RssEntryRepository) : RssEntryService {

    override fun saveAll(rssEntries : Collection<RssEntry>) {
        repository.saveAll(rssEntries)
    }

    override fun distinct(rssEntries: Collection<RssEntry>): Set<RssEntry> {
        val saved = repository.findAllByIdIn(rssEntries.map { it.id }).toSet()
        return rssEntries.filterNot { saved.contains(it) }.toSet()
    }


}