package inforgeon.inforgeon.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = ["rss.scheduler.enabled"], matchIfMissing = true)
class SchedulerConfig