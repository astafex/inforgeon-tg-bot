package inforgeon.inforgeon.entity

import inforgeon.inforgeon.constant.RssTopicName
import javax.persistence.*

/**
 * Сущность - настройки пользователя
 */
@Entity
@Table(schema = "bot", name = "disliked")
class DislikedTagCounter(

    @Id
    @GeneratedValue
    var id: Long? = null,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "topic", nullable = false, length = 20)
    var topic: RssTopicName,

    @Column(name = "tag", nullable = false, length = 100)
    var tag: String,

    @Column(name = "count", nullable = false)
    var count: Int,

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "settings_id")
    var settings: UserSettings? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DislikedTagCounter) return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return 31 * id.hashCode()
    }
}
