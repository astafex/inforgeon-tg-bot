package inforgeon.inforgeon.bot

import inforgeon.inforgeon.service.UserSettingsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class InforgeonBot : TelegramLongPollingBot() {
    @Autowired
    private lateinit var userSettingsService: UserSettingsService

    override fun getBotToken() = "6244400944:AAFKBxMZEImkHAKy4SjsrBVWtLiPg21C_zE"

    override fun getBotUsername() = "inforgeon_bot"

    override fun onUpdateReceived(update: Update) {
        val message = update.message
        val username = update.message.from.userName

        when (message.text) {
            "/start" -> {
//                if (userSettingsService.get(username) == null)
//                    userSettingsService.initializeUser(username)

                execute(
                    SendMessage().setChatId(message.chatId)
                        .setText("Привет! Я - бот Inforgeon, помогу тебе вырезать все ненужное из твоей новостной ленты.\n" +
                                 "Для начала работы со мной можно предварительно настроить твои любимые категории. Для этого выбери команду ...")
                )
            }
        }
    }
}