package inforgeon.inforgeon.entity

import javax.persistence.*

/**
 * Сущность - настройки пользователя
 */
@Entity
@Table(schema = "bot", name = "user_settings")
class UserSettings (

    // TODO исправить согласно user id АПИ телеграма
    @Id
    var username: String,

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    var dislikedTags: List<DislikedTagCounter> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserSettings) return false
        if (username != other.username) return false
        return true
    }

    override fun hashCode(): Int {
        return 31 * username.hashCode()
    }
}
