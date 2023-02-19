package inforgeon.integration

import inforgeon.checkEntryTags
import inforgeon.configureParsers
import inforgeon.inforgeon.constant.RssTopicName.JAVA
import inforgeon.inforgeon.entity.UserSettings
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional

/**
 * Интеграционные тесты со стандартными тестовыми настройками:
 *  - Отключенена категоризация по шедулеру
 *  - Отключен функционал бота, т.к. он ломится в телегу, а это нам не надо
 *  - Выставлена настройка в 2 дизлайк для блокировки новостей по подтеме
 */
@TestPropertySource(properties = ["rss.dislikes.threshold=2"])
class BotApiServiceThreshold2Test : AbstractIntegrationTest() {

    @Test
    @Transactional
            /**
             * Тестирование filterTag
             * Предусловие:
             * Эмулируем получение новостей через запуск категоризатора categorizer.rssCategorize() - тема JAVA
             * подтема JAVA8
             *      habr.com/ru/post/1/sdfsdf = springspring bootstarter
             * Дизлайкаем ниже порога
             * Получаем следующую новость по этой подтеме и проверяем на теги spring, autowired, service, controller
             * Пытаемся получить следующую новость по этой подтеме и получаем исключение - проверяем
             * Сбрасываем дислайки и получаем следующую новость по подтеме, проверяем ее на теги spring, spring boot, starter
             */
    fun filterTagTest() {
        configureParsers(
            JAVA, listOf(
                "habr.com/ru/post/2/sdfsdf" to "springspring bootstarter"
            )
        )

        categorizer.rssCategorize(JAVA)

        userSettingsService.initializeUser(UserSettings(1))

        botApiService.filterTag(1, JAVA, "spring")

        Assertions.assertThrows(NoSuchElementException::class.java) {
            botApiService.getNewestRssEntry(1, JAVA)
        }

    }


    /**
     * Тестирование дислайков
     * Предусловие:
     * Эмулируем получение новостей через запуск категоризатора categorizer.rssCategorize() - тема JAVA
     * подтема JAVA8
     *      habr.com/ru/post/2/sdfsdf = springspring bootstarter
     *      habr.com/ru/post/3/sdfsdf = springautowiredservicecontroller
     *      habr.com/ru/post/4/sdfsdf = springrepositoryjpahibernate
     * Получаем и дизлайкаем 1 новость, проверяем на теги spring, repository, jpa, hibernate
     * Получаем следующую новость по этой подтеме и проверяем на теги spring, autowired, service, controller
     * Пытаемся получить следующую новость по этой подтеме и получаем исключение - проверяем
     * Сбрасываем дислайки и получаем следующую новость по подтеме, проверяем ее на теги spring, spring boot, starter
     */
    @Test
    @Transactional
    fun complexDislikeTest() {
        configureParsers(
            JAVA, listOf(
                "habr.com/ru/post/2/sdfsdf" to "springspring bootstarter",
                "habr.com/ru/post/3/sdfsdf" to "springautowiredservicecontroller",
                "habr.com/ru/post/4/sdfsdf" to "springrepositoryjpahibernate"
            )
        )

        categorizer.rssCategorize(JAVA)

        userSettingsService.initializeUser(UserSettings(1))

        val first = botApiService.getNewestRssEntry(1, JAVA)

        Assertions.assertTrue(checkEntryTags(first, "spring", "repository", "jpa", "hibernate"))

        botApiService.dislikeRssEntry(1, JAVA, first.id)

        val second = botApiService.getNextRssEntry(1, JAVA, first.id)

        Assertions.assertTrue(checkEntryTags(second, "spring", "autowired", "service", "controller"))

        botApiService.dislikeRssEntry(1, JAVA, second.id)

        botApiService.dislikeRssEntry(1, JAVA, second.id)


        Assertions.assertThrows(NoSuchElementException::class.java) {
            botApiService.getNextRssEntry(1, JAVA, second.id)
        }

        botApiService.resetAllDislikes(1, JAVA)

        val third = botApiService.getNextRssEntry(1, JAVA, second.id)

        Assertions.assertTrue(checkEntryTags(third, "spring", "spring boot", "starter"))

    }


}