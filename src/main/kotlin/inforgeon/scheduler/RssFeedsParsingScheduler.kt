package inforgeon.inforgeon.scheduler

import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.rss.Categorizer
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RssFeedsParsingScheduler(private val categorizer: Categorizer) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES) // todo delay 4 minutes
//    @Scheduled(cron = "0 * * * * *")
    fun parsingAndCategorizedAllFeeds() {
        logger.info { "Начат парсинг и категоризация ленты..." }
        RssTopicName.values().forEach { topic ->
            categorizer.rssCategorize(topic)
        }
        logger.info { "Парсинг и категоризация ленты закончены" }
    }
}