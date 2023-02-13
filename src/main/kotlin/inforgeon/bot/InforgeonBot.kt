package inforgeon.inforgeon.bot

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

//@Service
class InforgeonBot : TelegramLongPollingBot() {

    override fun getBotToken() = "6244400944:AAFKBxMZEImkHAKy4SjsrBVWtLiPg21C_zE"

    override fun getBotUsername() = "inforgeon_bot"

    override fun onUpdateReceived(update: Update) {
        val message = update.message
        when (message.text)
        {
            "/start" -> {
                //зарегистрировать пользователя в бд
            }
        }
    }
}