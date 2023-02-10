package inforgeon

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
//    (exclude = [DataSourceAutoConfiguration::class])
@EnableJpaRepositories
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}