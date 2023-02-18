package inforgeon.integration

import inforgeon.checkEntryTags
import inforgeon.configureParsers
import inforgeon.inforgeon.constant.RssTopicName.JAVA
import inforgeon.inforgeon.entity.UserSettings
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional

/**
 * Интеграционные тесты со стандартными тестовыми настройками:
 *  - Отключенена категоризация по шедулеру
 *  - Отключен функционал бота, т.к. он ломится в телегу, а это нам не надо
 *  - Выставлена настройка в 1 дизлайк для блокировки новостей по подтеме
 */
internal class BotApiServiceStandartSettingsTest : AbstractIntegrationTest() {


    /**
     * Получение самой новой статьи по выбранному направлению, дизлайков нет
     * Предусловие:
     * Эмулируем следующий порядок получения новостей через запуск категоризатора categorizer.rssCategorize()
     *      habr.com/ru/post/1/sdfsdf = offcetdatetimejodaoptional
     *      habr.com/ru/post/2/sdfsdf = daodtopersistentity
     * Получаем самую свежую новость через botApiService.getNewestRssEntry()
     * Проверяем на вхождение тэгов "offcetdatetime, joda, optional" в статью
     */
    @Test
    @Transactional
    fun getNewestRssEntryTest() {
        configureParsers(JAVA, listOf("habr.com/ru/post/1/sdfsdf" to "offcetdatetimejodaoptional"))

        categorizer.rssCategorize(JAVA)

        userSettingsService.initializeUser(UserSettings(1))

        Assertions.assertTrue(
            checkEntryTags(
                botApiService.getNewestRssEntry(1, JAVA),
                "offcetdatetime", "joda", "optional"
            )
        )

    }

    /**
     * Получение самой новой статьи по выбранному направлению, дизлайки есть
     * Предусловие:
     * Эмулируем получение новости через запуск категоризатора categorizer.rssCategorize()
     *      habr.com/ru/post/1/sdfsdf = offcetdatetimejodaoptional
     * Дизлайкаем эту новость
     * Эмулируем получение следующих новостей через запуск категоризатора categorizer.rssCategorize()
     *      habr.com/ru/post/2/sdfsdf = offcetdatetimedaodtopersistentity
     *      habr.com/ru/post/3/sdfsdf = классоопoopинкапсуляц
     * Получаем самую свежую новость через botApiService.getNewestRssEntry()
     * Проверяем на вхождение тэгов "класс", "ооп", "oop", "инкапсуляц" в статью
     */
    @Test
    @Transactional
    fun getNewestRssEntryWitnDislikesTest() {
        configureParsers(JAVA, listOf("habr.com/ru/post/1/sdfsdf" to "offcetdatetimejodaoptional"))

        categorizer.rssCategorize(JAVA)

        userSettingsService.initializeUser(UserSettings(1))
        val first = botApiService.getNewestRssEntry(1, JAVA)

        botApiService.dislikeRssEntry(1, JAVA, first.id)

        configureParsers(
            JAVA, listOf(
                "habr.com/ru/post/2/sdfsdf" to "offcetdatetimedaodtopersistentity",
                "habr.com/ru/post/3/sdfsdf" to "классоопoopинкапсуляц"
            )
        )

        categorizer.rssCategorize(JAVA)

        Assertions.assertTrue(
            checkEntryTags(
                botApiService.getNewestRssEntry(1, JAVA),
                "класс", "ооп", "oop", "инкапсуляц"
            )
        )

    }

    /**
     * Тестирование сброса всех дислайков
     * Предусловие:
     * Эмулируем получение новости через запуск категоризатора categorizer.rssCategorize()
     *      habr.com/ru/post/1/sdfsdf = offcetdatetimejodaoptional
     * Дизлайкаем эту новость
     * Сбрасываем дислайки этого пользователя
     * Получаем самую свежую новость через botApiService.getNewestRssEntry()
     * Проверяем на вхождение тэгов "offcetdatetime", "joda", "optional" в статью
     */
    @Test
    @Transactional
    fun resetAllDislikesTest() {
        configureParsers(JAVA, listOf("habr.com/ru/post/1/sdfsdf" to "offcetdatetimejodaoptional"))

        categorizer.rssCategorize(JAVA)

        userSettingsService.initializeUser(UserSettings(1))

        val first = botApiService.getNewestRssEntry(1, JAVA)

        botApiService.dislikeRssEntry(1, JAVA, first.id)

        Assertions.assertThrows(NoSuchElementException::class.java) {
            botApiService.getNewestRssEntry(1, JAVA)
        }

        botApiService.resetAllDislikes(1, JAVA)

        Assertions.assertTrue(
            checkEntryTags(
                botApiService.getNewestRssEntry(1, JAVA),
                "offcetdatetime", "joda", "optional"
            )
        )

    }

}