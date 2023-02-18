package inforgeon.entity

import inforgeon.inforgeon.constant.RssSubtopicName
import inforgeon.inforgeon.constant.RssTopicName
import inforgeon.inforgeon.entity.UserSettings
import javax.persistence.*

/**
 * Сущность - RSS источник из ленты
 */
@Entity
@Table(schema = "bot", name = "rss_entry")
class RssEntry(
    @Id
    var id: Long = 0,

    @Column(name = "title", nullable = false, length = 256)
    var title: String,

    @Column(name = "author", length = 64)
    var author: String? = null,

    @Column(name = "url", nullable = false, length = 1024)
    var url: String,

    @Column(name = "description", length = 5000)
    var description: String? = null,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "topic", nullable = false, length = 20)
    var topic: RssTopicName,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "subtopic", nullable = false, length = 20)
    var subtopic: RssSubtopicName? = null,

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tags", joinColumns = [JoinColumn(name = "entry_id")])
    @Column(name = "tag", length = 100)
    var tags: List<String> = ArrayList(),

    @OneToMany(mappedBy = "currentRssEntry")
    var users: Set<UserSettings>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RssEntry) return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return 31 * id.hashCode()
    }
}
