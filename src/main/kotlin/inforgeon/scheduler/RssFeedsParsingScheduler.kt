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

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES) // delay 1 minute для тестов, для прома хватит и 5 мин
//    @Scheduled(cron = "0 * * * * *")
    fun parsingAndCategorizedAllFeeds() {
        logger.info { "Начат парсинг и категоризация ленты..." }
        RssTopicName.values().forEach { topic ->
            categorizer.rssCategorize(topic)
        }
        logger.info { "Парсинг и категоризация ленты закончены" }
    }
}