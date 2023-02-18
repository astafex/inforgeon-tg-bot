package inforgeon.inforgeon.entity

import inforgeon.entity.RssEntry
import javax.persistence.*

/**
 * Сущность - настройки пользователя
 */
@Entity
@Table(schema = "bot", name = "user_settings")
class UserSettings (
    @Id
    var id: Long,

    @ManyToOne
    @JoinColumn(name="current_rss_entry_id")
    var currentRssEntry: RssEntry? = null,

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_settings_id")
    var dislikedTags: List<DislikedTagCounter> = mutableListOf()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserSettings) return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return 31 * id.hashCode()
    }
}
