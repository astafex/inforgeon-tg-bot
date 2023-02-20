package inforgeon.bot

import inforgeon.entity.RssEntry
import inforgeon.inforgeon.bot.InforgeonBot
import inforgeon.inforgeon.constant.RssSubtopicName
import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.entity.UserSettings
import inforgeon.inforgeon.service.BotApiService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.spyk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

class InforgeonBotTest {

    @MockK
    private lateinit var service: BotApiService

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this)
        every { service.getUserSettings(any()) } returns UserSettings(1)
    }

    @Test
    fun startTest() {
        val inforgeonBot = spyk(InforgeonBot("token", "name", service))
        val update = Update().apply {
            message = Message().apply {
                from = User().apply { id = 1 }
                text = "/start"
                chat = Chat(1, "private")
                messageId = 123
            }
        }

        val slot = slot<SendMessage>()
        every { inforgeonBot.execute(capture(slot)) } answers {
            println(slot)
            Message()
        }

        inforgeonBot.onUpdateReceived(update)

        Assertions.assertEquals("Основное меню", slot.captured.text)

        val keyboardMarkup = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[0]
        Assertions.assertEquals(2, keyboardMarkup.size)
        Assertions.assertEquals(ChatButton.CATEGORY_NEWS_LINE.text, keyboardMarkup[0].text)
        Assertions.assertEquals(ChatButton.RESET_DISLIKES.text, keyboardMarkup[1].text)
    }

    @Test
    fun undefinedTextTest() {
        val inforgeonBot = spyk(InforgeonBot("token", "name", service))
        val update = Update().apply {
            message = Message().apply {
                from = User().apply { id = 1 }
                text = "/test"
                chat = Chat(1, "private")
                messageId = 123
            }
        }

        val slot = slot<SendMessage>()
        every { inforgeonBot.execute(capture(slot)) } answers {
            println(slot)
            Message()
        }

        inforgeonBot.onUpdateReceived(update)

        Assertions.assertEquals(
            "Я такие слова еще не знаю.\n" +
                "                        Может попробуете что-то из этого?", slot.captured.text.trim()
        )

        val keyboardMarkup = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[0]
        Assertions.assertEquals(2, keyboardMarkup.size)
        Assertions.assertEquals(ChatButton.CATEGORY_NEWS_LINE.text, keyboardMarkup[0].text)
        Assertions.assertEquals(ChatButton.RESET_DISLIKES.text, keyboardMarkup[1].text)
    }

    @Test
    fun mainMenuTest() {
        val inforgeonBot = spyk(InforgeonBot("token", "name", service))
        val update = Update().apply {
            callbackQuery = CallbackQuery().apply {
                from = User().apply { id = 1 }
                data = "MAIN_MENU"
                message = Message().apply {
                    messageId = 123
                    chat = Chat(1, "private")
                }
            }
        }

        val slot = slot<SendMessage>()
        every { inforgeonBot.execute(capture(slot)) } answers {
            println(slot)
            Message()
        }

        inforgeonBot.onUpdateReceived(update)

        Assertions.assertEquals("Основное меню", slot.captured.text)

        val keyboardMarkup = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[0]
        Assertions.assertEquals(2, keyboardMarkup.size)
        Assertions.assertEquals(ChatButton.CATEGORY_NEWS_LINE.text, keyboardMarkup[0].text)
        Assertions.assertEquals(ChatButton.RESET_DISLIKES.text, keyboardMarkup[1].text)
    }

    @Test
    fun resetDislikesTest() {
        val inforgeonBot = spyk(InforgeonBot("token", "name", service))
        val update = Update().apply {
            callbackQuery = CallbackQuery().apply {
                from = User().apply { id = 1 }
                data = "RESET_DISLIKES"
                message = Message().apply {
                    messageId = 123
                    chat = Chat(1, "private")
                }
            }
        }

        every { service.resetAllDislikes(any(), any()) } returns Unit

        val slot = slot<SendMessage>()
        every { inforgeonBot.execute(capture(slot)) } answers {
            println(slot)
            Message()
        }

        inforgeonBot.onUpdateReceived(update)

        Assertions.assertEquals("Дизлайки успешно сброшены!", slot.captured.text)

        val keyboardMarkup = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[0]
        Assertions.assertEquals(2, keyboardMarkup.size)
        Assertions.assertEquals(ChatButton.CATEGORY_NEWS_LINE.text, keyboardMarkup[0].text)
        Assertions.assertEquals(ChatButton.RESET_DISLIKES.text, keyboardMarkup[1].text)
    }

    @Test
    fun categoryNewLineTest() {
        val inforgeonBot = spyk(InforgeonBot("token", "name", service))
        val update = Update().apply {
            callbackQuery = CallbackQuery().apply {
                from = User().apply { id = 1 }
                data = "CATEGORY_NEWS_LINE"
                message = Message().apply {
                    messageId = 123
                    chat = Chat(1, "private")
                }
            }
        }

        val slot = slot<SendMessage>()
        every { inforgeonBot.execute(capture(slot)) } answers {
            println(slot)
            Message()
        }

        inforgeonBot.onUpdateReceived(update)

        Assertions.assertEquals("Выберете тему новостей:", slot.captured.text)

        val keyboardMarkup1 = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[0]
        Assertions.assertEquals(3, keyboardMarkup1.size)
        Assertions.assertEquals(ChatButton.JAVA.text, keyboardMarkup1[0].text)
        Assertions.assertEquals(ChatButton.KOTLIN.text, keyboardMarkup1[1].text)
        Assertions.assertEquals(ChatButton.NEWS.text, keyboardMarkup1[2].text)

        val keyboardMarkup2 = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[1]
        Assertions.assertEquals(1, keyboardMarkup2.size)
        Assertions.assertEquals(ChatButton.MAIN_MENU.text, keyboardMarkup2[0].text)
    }

    @Test
    fun javaTest() {
        val inforgeonBot = spyk(InforgeonBot("token", "name", service))
        val update = Update().apply {
            callbackQuery = CallbackQuery().apply {
                from = User().apply { id = 1 }
                data = "JAVA"
                message = Message().apply {
                    messageId = 123
                    chat = Chat(1, "private")
                }
            }
        }

        every { service.getNewestRssEntry(any(), any()) } returns RssEntry(
            title = "Java NEWS",
            url = "testURL",
            topic = RssTopicName.JAVA,
            author = "testAuthor",
            subtopic = RssSubtopicName.BACKEND
        )
        every { service.saveUserSettings(any()) } returns UserSettings(1)

        val slot = slot<SendMessage>()
        every { inforgeonBot.execute(capture(slot)) } answers {
            println(slot)
            Message()
        }

        inforgeonBot.onUpdateReceived(update)

        Assertions.assertEquals(
            "Автор: testAuthor\n" +
                "Категория: JAVA\n" +
                "Раздел: BACKEND\n" +
                "Ссылка: testURL", slot.captured.text
        )

        val keyboardMarkup1 = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[0]
        Assertions.assertEquals(2, keyboardMarkup1.size)
        Assertions.assertEquals(ChatButton.NEXT.text, keyboardMarkup1[0].text)
        Assertions.assertEquals(ChatButton.DISLIKE.text, keyboardMarkup1[1].text)

        val keyboardMarkup2 = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[1]
        Assertions.assertEquals(1, keyboardMarkup2.size)
        Assertions.assertEquals(ChatButton.MAIN_MENU.text, keyboardMarkup2[0].text)
    }

    @Test
    fun kotlinTest() {
        val inforgeonBot = spyk(InforgeonBot("token", "name", service))
        val update = Update().apply {
            callbackQuery = CallbackQuery().apply {
                from = User().apply { id = 1 }
                data = "KOTLIN"
                message = Message().apply {
                    messageId = 123
                    chat = Chat(1, "private")
                }
            }
        }

        every { service.getNewestRssEntry(any(), any()) } returns RssEntry(
            title = "KOTLIN NEWS",
            url = "testURL",
            topic = RssTopicName.KOTLIN,
            author = "testAuthor",
            subtopic = RssSubtopicName.BACKEND
        )
        every { service.saveUserSettings(any()) } returns UserSettings(1)

        val slot = slot<SendMessage>()
        every { inforgeonBot.execute(capture(slot)) } answers {
            println(slot)
            Message()
        }

        inforgeonBot.onUpdateReceived(update)

        Assertions.assertEquals(
            "Автор: testAuthor\n" +
                "Категория: KOTLIN\n" +
                "Раздел: BACKEND\n" +
                "Ссылка: testURL", slot.captured.text
        )

        val keyboardMarkup1 = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[0]
        Assertions.assertEquals(2, keyboardMarkup1.size)
        Assertions.assertEquals(ChatButton.NEXT.text, keyboardMarkup1[0].text)
        Assertions.assertEquals(ChatButton.DISLIKE.text, keyboardMarkup1[1].text)

        val keyboardMarkup2 = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[1]
        Assertions.assertEquals(1, keyboardMarkup2.size)
        Assertions.assertEquals(ChatButton.MAIN_MENU.text, keyboardMarkup2[0].text)
    }

    @Test
    fun newsTest() {
        val inforgeonBot = spyk(InforgeonBot("token", "name", service))
        val update = Update().apply {
            callbackQuery = CallbackQuery().apply {
                from = User().apply { id = 1 }
                data = "NEWS"
                message = Message().apply {
                    messageId = 123
                    chat = Chat(1, "private")
                }
            }
        }

        every { service.getNewestRssEntry(any(), any()) } returns RssEntry(
            title = "NEWS",
            url = "testURL",
            topic = RssTopicName.NEWS,
            author = "testAuthor",
            subtopic = RssSubtopicName.ANDROID
        )
        every { service.saveUserSettings(any()) } returns UserSettings(1)

        val slot = slot<SendMessage>()
        every { inforgeonBot.execute(capture(slot)) } answers {
            println(slot)
            Message()
        }

        inforgeonBot.onUpdateReceived(update)

        Assertions.assertEquals(
            "Автор: testAuthor\n" +
                "Категория: NEWS\n" +
                "Раздел: ANDROID\n" +
                "Ссылка: testURL", slot.captured.text
        )

        val keyboardMarkup1 = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[0]
        Assertions.assertEquals(2, keyboardMarkup1.size)
        Assertions.assertEquals(ChatButton.NEXT.text, keyboardMarkup1[0].text)
        Assertions.assertEquals(ChatButton.DISLIKE.text, keyboardMarkup1[1].text)

        val keyboardMarkup2 = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[1]
        Assertions.assertEquals(1, keyboardMarkup2.size)
        Assertions.assertEquals(ChatButton.MAIN_MENU.text, keyboardMarkup2[0].text)
    }

    @Test
    fun nextTest() {
        val inforgeonBot = spyk(InforgeonBot("token", "name", service))
        val update = Update().apply {
            callbackQuery = CallbackQuery().apply {
                from = User().apply { id = 1 }
                data = "NEXT"
                message = Message().apply {
                    messageId = 123
                    chat = Chat(1, "private")
                }
            }
        }

        every { service.getUserSettings(any()) } returns UserSettings(1).apply {
            currentRssEntry = RssEntry(
                title = "KOTLIN NEWS",
                url = "testURL",
                topic = RssTopicName.KOTLIN,
                author = "testAuthor",
                subtopic = RssSubtopicName.BACKEND
            )
        }
        every { service.getNextRssEntry(any(), any(), any()) } returns RssEntry(
            title = "NEWS",
            url = "testURL2",
            topic = RssTopicName.KOTLIN,
            author = "testAuthor2",
            subtopic = RssSubtopicName.ANDROID
        )

        every { service.saveUserSettings(any()) } returns UserSettings(1)

        val slot = slot<SendMessage>()
        every { inforgeonBot.execute(capture(slot)) } answers {
            println(slot)
            Message()
        }

        inforgeonBot.onUpdateReceived(update)

        Assertions.assertEquals(
            "Автор: testAuthor2\n" +
                "Категория: KOTLIN\n" +
                "Раздел: ANDROID\n" +
                "Ссылка: testURL2", slot.captured.text
        )

        val keyboardMarkup1 = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[0]
        Assertions.assertEquals(2, keyboardMarkup1.size)
        Assertions.assertEquals(ChatButton.NEXT.text, keyboardMarkup1[0].text)
        Assertions.assertEquals(ChatButton.DISLIKE.text, keyboardMarkup1[1].text)

        val keyboardMarkup2 = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[1]
        Assertions.assertEquals(1, keyboardMarkup2.size)
        Assertions.assertEquals(ChatButton.MAIN_MENU.text, keyboardMarkup2[0].text)
    }

    @Test
    fun dislikeTest() {
        val inforgeonBot = spyk(InforgeonBot("token", "name", service))
        val update = Update().apply {
            callbackQuery = CallbackQuery().apply {
                from = User().apply { id = 1 }
                data = "NEWS"
                message = Message().apply {
                    messageId = 123
                    chat = Chat(1, "private")
                }
            }
        }

        every { service.getUserSettings(any()) } returns UserSettings(1).apply {
            currentRssEntry = RssEntry(
                title = "KOTLIN NEWS",
                url = "testURL",
                topic = RssTopicName.KOTLIN,
                author = "testAuthor",
                subtopic = RssSubtopicName.BACKEND
            )
        }
        every { service.getNewestRssEntry(any(), any()) } returns RssEntry(
            title = "NEWS",
            url = "testURL2",
            topic = RssTopicName.KOTLIN,
            author = "testAuthor2",
            subtopic = RssSubtopicName.ANDROID
        )
        every { service.dislikeRssEntry(any(), any(), any()) } returns Unit
        every { service.saveUserSettings(any()) } returns UserSettings(1)

        val slot = slot<SendMessage>()
        every { inforgeonBot.execute(capture(slot)) } answers {
            println(slot)
            Message()
        }

        inforgeonBot.onUpdateReceived(update)

        Assertions.assertEquals(
            "Автор: testAuthor2\n" +
                "Категория: KOTLIN\n" +
                "Раздел: ANDROID\n" +
                "Ссылка: testURL2", slot.captured.text
        )

        val keyboardMarkup1 = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[0]
        Assertions.assertEquals(2, keyboardMarkup1.size)
        Assertions.assertEquals(ChatButton.NEXT.text, keyboardMarkup1[0].text)
        Assertions.assertEquals(ChatButton.DISLIKE.text, keyboardMarkup1[1].text)

        val keyboardMarkup2 = (slot.captured.replyMarkup as InlineKeyboardMarkup).keyboard[1]
        Assertions.assertEquals(1, keyboardMarkup2.size)
        Assertions.assertEquals(ChatButton.MAIN_MENU.text, keyboardMarkup2[0].text)
    }

}