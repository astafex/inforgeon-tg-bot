package inforgeon.bot

enum class ChatButton(val text: String) {
    CATEGORY_NEWS_LINE("Новости по теме"),
    JAVA("JAVA"),
    KOTLIN("KOTLIN"),
    NEWS("NEWS"),

    RESET_DISLIKES("Сброс дизлайков"),
    NEXT("Следующая новость"),
    DISLIKE("\uD83D\uDC4E"),
    MAIN_MENU("Главное меню"),
}

enum class ChatCommand(val command: String, val text: String) {
    START("/start", "Начать чат с ботом")
}
