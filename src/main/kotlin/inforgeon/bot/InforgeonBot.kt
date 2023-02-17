package inforgeon.inforgeon.bot

import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.*
import inforgeon.inforgeon.service.BotApiService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import javax.annotation.PostConstruct


@Component
class InforgeonBot(
    @Value("\${inforgeon-bot.token}") private val token: String,
    @Value("\${inforgeon-bot.name}") private val name: String,
    private val service: BotApiService
) : TelegramLongPollingBot(token) {
    private val log = KotlinLogging.logger {}

    @PostConstruct
    fun registerBot() {
        TelegramBotsApi(DefaultBotSession::class.java).registerBot(this)
        log.info { "Started bot $name, token: $token" }
    }

    override fun getBotUsername() = name

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()) {
            handleMessage(update);
        }

        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update)
        }
    }


    private fun handleMessage(update: Update) {
        update.message.also { message ->
            service.getUserSettings(message.from.id)

            when (message.text) {
                "/start" -> {
                    deleteMessage(message)
                    goMainMenu(message)
                }
            }
        }
    }

    private fun handleCallbackQuery(update: Update) {
        update.callbackQuery.also { callbackQuery ->
            service.getUserSettings(callbackQuery.from.id)

            when (callbackQuery.data) {
                "CATEGORY" -> goCategory(callbackQuery)

                "JAVA" -> goNewestNews(callbackQuery, RssTopicName.JAVA)

                "KOTLIN" -> goNewestNews(callbackQuery, RssTopicName.KOTLIN)

                "NEWS" -> goNewestNews(callbackQuery, RssTopicName.NEWS)

                "NEXT" -> goNextNews(callbackQuery)

                "DISLIKE" -> goDislikeRssEntry(callbackQuery)

                "MAIN_MENU" -> goMainMenu(callbackQuery.message)
            }
        }
    }

    private fun goCategory(callbackQuery: CallbackQuery) {
        execute(
            SendMessage().apply {
                chatId = callbackQuery.message.chatId.toString()
                text = "Выберете категорию"
                replyMarkup = InlineKeyboardMarkup().also { keyboardMarkup ->
                    keyboardMarkup.keyboard = listOf(
                        listOf(
                            InlineKeyboardButton("JAVA").apply { callbackData = "JAVA" },
                            InlineKeyboardButton("KOTLIN").apply { callbackData = "KOTLIN" },
                            InlineKeyboardButton("NEWS").apply { callbackData = "NEWS" }
                        )
                    )
                }
            }
        )
    }

    private fun goNewestNews(callbackQuery: CallbackQuery, rssTopicName: RssTopicName) {
        val rssEntry = service.getNewestRssEntry(callbackQuery.message.chatId, rssTopicName)

        service.getUserSettings(callbackQuery.from.id)!!.also {
            service.saveUserSettings(it.apply { currentRssEntry = rssEntry })
        }
        execute(callbackQuery.message, rssEntry)

    }

    private fun goNextNews(callbackQuery: CallbackQuery) {
        val userSettings = service.getUserSettings(callbackQuery.from.id)!!
        val rssEntry = service.getNextRssEntry(
            userSettings.id,
            userSettings.currentRssEntry!!.topic,
            userSettings.currentRssEntry!!.id
        )

        service.saveUserSettings(userSettings.apply { currentRssEntry = rssEntry })
        execute(callbackQuery.message, rssEntry)
    }

    private fun execute(message: Message, rssEntry: RssEntry) {
        execute(
            SendMessage().apply {
                chatId = message.chatId.toString()
                text = prettyRssEntry(rssEntry)
                replyMarkup = InlineKeyboardMarkup().also { keyboardMarkup ->
                    keyboardMarkup.keyboard = listOf(
                        listOf(
                            InlineKeyboardButton("Следующая").apply { callbackData = "NEXT" },
                            InlineKeyboardButton("\uD83D\uDC4E").apply { callbackData = "DISLIKE" },
                        ),
                        listOf(
                            InlineKeyboardButton("Главное меню").apply { callbackData = "MAIN_MENU" },
                        )
                    )
                }
            }
        )
    }

    private fun goDislikeRssEntry(callbackQuery: CallbackQuery) {
        val userSettings = service.getUserSettings(callbackQuery.from.id)!!
        service.dislikeRssEntry(
            userSettings.id,
            userSettings.currentRssEntry!!.topic,
            userSettings.currentRssEntry!!.id
        )
//        deleteMessage(callbackQuery.message)
//        goNextNews(callbackQuery)
    }

    private fun goMainMenu(message: Message) {
        execute(
            SendMessage().apply {
                chatId = message.chatId.toString()
                text = "Основное меню"
                replyMarkup = InlineKeyboardMarkup().also { keyboardMarkup ->
                    keyboardMarkup.keyboard = listOf(
                        listOf(
                            InlineKeyboardButton("Новости по теме").apply { callbackData = "CATEGORY" },
                            InlineKeyboardButton("Сброс дизлайков").apply { callbackData = "RESET_DISLIKES" },
                            InlineKeyboardButton("Не хочу смотреть").apply { callbackData = "MANUAL_DISLIKE" }
                        )
                    )
                }
            }
        )
    }

    private fun deleteMessage(message: Message) {
        execute(
            DeleteMessage().apply {
                chatId = message.chatId.toString()
                messageId = message.messageId
            }
        )
    }

    private fun prettyRssEntry(rssEntry: RssEntry) =
        """
            Заголовок: ${rssEntry.title}
            Автор: ${rssEntry.author}
            Категория: ${rssEntry.topic}
            Раздел: ${rssEntry.subtopic}
            Ссылка: ${rssEntry.url}
        """.trimIndent()
}
