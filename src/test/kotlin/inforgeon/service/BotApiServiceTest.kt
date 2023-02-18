package inforgeon.service

import inforgeon.BaseSpringTest
import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.service.BotApiService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

@Transactional
@Sql("classpath:/sql/populate.sql")
internal class BotApiServiceTest : BaseSpringTest() {

    @Autowired
    private lateinit var botApiService: BotApiService

    val testUser = 1L
    val newsEntryId1 = 3716270L
    val newsEntryId2 = 3716268L
    val newsEntryId3 = 3716262L

    @Test
    fun userRegistration() {
        val settings = botApiService.getUserSettings(testUser)
        assertNotNull(settings)
        assertEquals(testUser, settings?.id)
    }

    @Test
    @Sql("classpath:/sql/populateUser.sql")
    fun getNewestRssEntry_first() {
        // приходит первый
        val newestRssEntry = botApiService.getNewestRssEntry(testUser, RssTopicName.NEWS)
        assertNotNull(newestRssEntry)
        assertEquals(newsEntryId1, newestRssEntry.id)
    }

    @Test
    @Sql("classpath:/sql/populateUser_firstEntryDisliked.sql")
    fun getNewestRssEntry_second() {
        // пропускается первый, приходит второй
        val newestRssEntry = botApiService.getNewestRssEntry(testUser, RssTopicName.NEWS)
        assertNotNull(newestRssEntry)
        assertEquals(newsEntryId2, newestRssEntry.id)
    }

    @Test
    @Sql("classpath:/sql/populateUser.sql")
    fun getNextRssEntry_second() {
        // при шаге с первого приходит второй
        val nextRssEntry = botApiService.getNextRssEntry(testUser, RssTopicName.NEWS, 3716270)
        assertNotNull(nextRssEntry)
        assertEquals(newsEntryId2, nextRssEntry.id)
    }

    @Test
    @Sql("classpath:/sql/populateUser_secondEntryDisliked.sql")
    fun getNextRssEntry_third() {
        // при шаге с первого пропускается второй, приходит третий
        val nextRssEntry = botApiService.getNextRssEntry(testUser, RssTopicName.NEWS, 3716270)
        assertNotNull(nextRssEntry)
        assertEquals(newsEntryId3, nextRssEntry.id)
    }

    @Test
    @Sql("classpath:/sql/populateUser.sql")
    fun dislikeRssEntry() {
        // дизлайкнуть первый, лента должна начаться со второго
        botApiService.dislikeRssEntry(testUser, RssTopicName.NEWS, newsEntryId1)
        // для теста свитч-кейса
        botApiService.dislikeRssEntry(testUser, RssTopicName.NEWS, newsEntryId1)
        val newestRssEntry = botApiService.getNewestRssEntry(testUser, RssTopicName.NEWS)
        assertNotNull(newestRssEntry)
        assertEquals(newsEntryId2, newestRssEntry.id)
    }

    @Test
    @Sql("classpath:/sql/populateUser.sql")
    fun filterTag() {
        // заминусить тэг из первого, лента должна начаться со второго
        botApiService.filterTag(testUser, RssTopicName.NEWS, "hh")
        // для теста свитч-кейса
        botApiService.filterTag(testUser, RssTopicName.NEWS, "hh")
        val newestRssEntry = botApiService.getNewestRssEntry(testUser, RssTopicName.NEWS)
        assertNotNull(newestRssEntry)
        assertEquals(newsEntryId2, newestRssEntry.id)
    }

    @Test
    @Sql("classpath:/sql/populateUser_firstEntryDisliked.sql")
    fun resetAllDislikes() {
        // стереть заминусованный тэг из первого, лента должна начаться с первого
        botApiService.resetAllDislikes(testUser, RssTopicName.NEWS)
        val newestRssEntry = botApiService.getNewestRssEntry(testUser, RssTopicName.NEWS)
        assertNotNull(newestRssEntry)
        assertEquals(newsEntryId1, newestRssEntry.id)
    }
}