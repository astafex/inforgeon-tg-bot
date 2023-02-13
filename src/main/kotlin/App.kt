package inforgeon

import inforgeon.inforgeon.bot.InforgeonBot
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.meta.TelegramBotsApi

@SpringBootApplication
//    (exclude = [DataSourceAutoConfiguration::class])
@EnableJpaRepositories
class App

fun main(args: Array<String>) {
    ApiContextInitializer.init()
    TelegramBotsApi().registerBot(InforgeonBot())
    runApplication<App>(*args)
}