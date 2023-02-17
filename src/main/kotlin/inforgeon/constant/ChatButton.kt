package inforgeon.inforgeon.constant

enum class ButtonMainMenu(val text: String) {
    GET_NEWS("Новости по теме"),
    RESET_DISLIKES("Сброс дизлайков"),
    MANUAL_DISLIKE_TAG("Не хочу смотреть"),
}

enum class ButtonNewsCategoryMenu {
    JAVA, KOTLIN, NEWS
}

enum class ButtonNewsLineMenu(val text: String) {
    DISLIKE("Dislike"),
    NEXT("Следующая"),
    MAIN_MENU("Главное меню")
}

