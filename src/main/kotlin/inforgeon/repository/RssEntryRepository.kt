package inforgeon.repository

import inforgeon.entity.RssEntry
import org.springframework.data.jpa.repository.JpaRepository


interface RssEntryRepository : JpaRepository<RssEntry, Long> {
}