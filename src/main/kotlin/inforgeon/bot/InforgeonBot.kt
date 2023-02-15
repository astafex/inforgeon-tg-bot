package inforgeon.inforgeon.bot

import inforgeon.inforgeon.service.BotApiService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import javax.annotation.PostConstruct


@Component
class InforgeonBot(
    @Value("\${inforgeon-bot.token}") private val token: String,
    @Value("\${inforgeon-bot.name}") private val name: String,
    private val botApiService: BotApiService
) : TelegramLongPollingBot(token) {
    private val log = KotlinLogging.logger {}

    @PostConstruct
    fun registerBot() {
        TelegramBotsApi(DefaultBotSession::class.java).registerBot(this)
        log.info { "Started bot $name, token: $token" }
    }

    override fun getBotUsername() = name

    override fun onUpdateReceived(update: Update) {
        update.apply {
            if (hasMessage()) {
                val user = botApiService.userRegistration(message.from.id)



            }
        }

        if (update.hasMessage()) {
            val currentMessage: Message = update.message
            val currentChatId: Long = currentMessage.chatId

            val keyboard = ReplyKeyboardMarkup()
            keyboard.keyboard = listOf(
                KeyboardRow().apply {
                    add(KeyboardButton("TEST1"))
                },
                KeyboardRow().apply {
                    add(KeyboardButton("TEST2"))
                }
            )


            when (currentMessage.text) {
                "/start" -> {
                    execute(
                        SendMessage().apply {
                            replyMarkup = keyboard
                            chatId = currentChatId.toString()
                            text = "user: ${currentMessage.from.userName}, id: ${currentMessage.from.id}"
                        }
                    )
                }

                "/some" -> {
                    execute(
                        SendMessage().apply {
                            replyMarkup = InlineKeyboardMarkup().also {
                                it.keyboard = listOf(
                                    listOf(
                                        InlineKeyboardButton("LOL1").apply { callbackData = "LOLLL1" },
                                        InlineKeyboardButton("LOL2").apply { callbackData = "LOLLL2" },
                                    )
                                )
                            }
                            chatId = currentChatId.toString()
                            text = "Choice:"

                        }
                    )
                }
                /*                    if (userSettingsService.get(username) == null)
                                        userSettingsService.initializeUser(username)

                                    execute(

                                        SendMessage()
                                            .setChatId(message.chatId)
                                            .setText(
                                                "Привет! Я - бот Inforgeon, помогу тебе вырезать все ненужное из твоей новостной ленты.\n" +
                                                        "Для начала работы со мной можно предварительно настроить твои любимые категории. Для этого выбери команду ..."
                                            )
                                    )*/
            }
        }
    }
}