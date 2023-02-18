package inforgeon.inforgeon.bot

import inforgeon.bot.ChatButton
import inforgeon.bot.ChatCommand
import inforgeon.entity.RssEntry
import inforgeon.inforgeon.constant.RssTopicName
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
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
            service.getUserSettings(update.message.from.id)
            handleMessage(update)

            deleteMessage(update.message)
        }

        if (update.hasCallbackQuery()) {
            service.getUserSettings(update.callbackQuery.from.id)
            handleCallbackQuery(update)

            deleteMessage(update.callbackQuery.message)
        }
    }

    private fun handleMessage(update: Update) {
        update.message.also { message ->
            with(message.text) {
                when {
                    ChatCommand.START.command == this -> goMainMenu(message)
                    else -> goMainMenu(
                        message, """
                        Я такие слова еще не знаю.
                        Может попробуете что-то из этого?""".trimMargin()
                    )
                }
            }
        }
    }

    private fun handleCallbackQuery(update: Update) {
        update.callbackQuery.also { callbackQuery ->
            when (callbackQuery.data) {
                ChatButton.MAIN_MENU.name -> goMainMenu(callbackQuery.message)
                ChatButton.RESET_DISLIKES.name -> goResetDislikes(callbackQuery)

                ChatButton.CATEGORY_NEWS_LINE.name -> goCategoryNewsLine(callbackQuery)
                ChatButton.JAVA.name -> goNewestNews(callbackQuery, RssTopicName.JAVA)
                ChatButton.KOTLIN.name -> goNewestNews(callbackQuery, RssTopicName.KOTLIN)
                ChatButton.NEWS.name -> goNewestNews(callbackQuery, RssTopicName.NEWS)
                ChatButton.NEXT.name -> goNextNews(callbackQuery)
                ChatButton.DISLIKE.name -> goDislikeRssEntry(callbackQuery)
            }
        }
    }

    private fun goMainMenu(message: Message, textMessage: String = "Основное меню") {
        execute(SendMessage().apply {
            chatId = message.chatId.toString()
            text = textMessage

            replyMarkup = InlineKeyboardMarkup().also { keyboardMarkup ->
                keyboardMarkup.keyboard = listOf(
                    listOf(
                        ChatButton.CATEGORY_NEWS_LINE.asInlineKeyboardButton(),
                        ChatButton.RESET_DISLIKES.asInlineKeyboardButton()
                    )
                )
            }
        })
    }

    private fun goCategoryNewsLine(callbackQuery: CallbackQuery) {
        execute(SendMessage().apply {
            chatId = callbackQuery.message.chatId.toString()
            text = "Выберете тему новостей:"
            replyMarkup = InlineKeyboardMarkup().also { keyboardMarkup ->
                keyboardMarkup.keyboard = listOf(
                    listOf(
                        ChatButton.JAVA.asInlineKeyboardButton(),
                        ChatButton.KOTLIN.asInlineKeyboardButton(),
                        ChatButton.NEWS.asInlineKeyboardButton()
                    ),
                    listOf(
                        ChatButton.MAIN_MENU.asInlineKeyboardButton(),
                    )
                )
            }
        })
    }

    private fun goResetDislikes(callbackQuery: CallbackQuery) {
        RssTopicName.values().forEach { rssTopic ->
            service.resetAllDislikes(callbackQuery.from.id, rssTopic)
        }
        goMainMenu(callbackQuery.message, "Дизлайки успешно сброшены!")
    }

    private fun goNewestNews(callbackQuery: CallbackQuery, rssTopic: RssTopicName) {
        try {
            val rssEntry: RssEntry = service.getNewestRssEntry(callbackQuery.message.chatId, rssTopic)
            service.getUserSettings(callbackQuery.from.id)!!.also {
                service.saveUserSettings(it.apply { currentRssEntry = rssEntry })
            }
            getNews(callbackQuery.message, rssEntry)

        } catch (e: NoSuchElementException) {
            goNoNewNews(callbackQuery, rssTopic)
        }
    }

    private fun goNextNews(callbackQuery: CallbackQuery) {
        val userSettings = service.getUserSettings(callbackQuery.from.id)!!
        try {
            val rssEntry: RssEntry = service.getNextRssEntry(
                userSettings.id,
                userSettings.currentRssEntry!!.topic,
                userSettings.currentRssEntry!!.id
            )

            service.saveUserSettings(userSettings.apply { currentRssEntry = rssEntry })
            getNews(callbackQuery.message, rssEntry)
        } catch (e: NoSuchElementException) {
            goNoNewNews(callbackQuery, userSettings.currentRssEntry!!.topic)
        }
    }

    private fun getNews(message: Message, rssEntry: RssEntry) {
        execute(SendMessage().apply {
            chatId = message.chatId.toString()
            text = rssEntry.asPrettyString()
            replyMarkup = InlineKeyboardMarkup().also { keyboardMarkup ->
                keyboardMarkup.keyboard = listOf(
                    listOf(
                        ChatButton.NEXT.asInlineKeyboardButton(),
                        ChatButton.DISLIKE.asInlineKeyboardButton(),
                    ),
                    listOf(
                        ChatButton.MAIN_MENU.asInlineKeyboardButton()
                    )
                )
            }
        })
    }

    private fun goNoNewNews(callbackQuery: CallbackQuery, rssTopic: RssTopicName) {
        execute(SendMessage().apply {
            chatId = callbackQuery.message.chatId.toString()
            text = """
                    К сожалению, новых новостей по теме ${rssTopic.name} нет.
                    Вы можете выбрать другую тему или сбросить дизлайки
                    """.trimIndent()
            replyMarkup = InlineKeyboardMarkup().also { keyboardMarkup ->
                keyboardMarkup.keyboard = listOf(
                    listOf(
                        ChatButton.CATEGORY_NEWS_LINE.asInlineKeyboardButton(),
                        ChatButton.RESET_DISLIKES.asInlineKeyboardButton(),
                    ),
                    listOf(
                        ChatButton.MAIN_MENU.asInlineKeyboardButton()
                    )
                )
            }
        })
    }

    private fun goDislikeRssEntry(callbackQuery: CallbackQuery) {
        val userSettings = service.getUserSettings(callbackQuery.from.id)!!
        service.dislikeRssEntry(
            userSettings.id,
            userSettings.currentRssEntry!!.topic,
            userSettings.currentRssEntry!!.id
        )
        goNextNews(callbackQuery)
    }

    private fun deleteMessage(message: Message) {
        (message.messageId - 1..message.messageId).forEach {
            try {
                execute(DeleteMessage().apply {
                    chatId = message.chatId.toString()
                    messageId = it
                })
            } catch (exception: TelegramApiRequestException) {
                log.debug { exception }
            }
        }
    }

    private fun ChatButton.asInlineKeyboardButton() =
        InlineKeyboardButton(text).apply { callbackData = name }

    private fun RssEntry.asPrettyString() = """
            Автор: $author
            Категория: $topic
            Раздел: $subtopic
            Ссылка: $url
        """.trimIndent()
}
