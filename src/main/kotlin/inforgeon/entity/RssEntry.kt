package inforgeon.entity

import inforgeon.inforgeon.constant.RssSuptopicName
import inforgeon.inforgeon.constant.RssTopicName
import javax.persistence.*

@Entity
@Table(schema = "bot", name = "rss_entry")
class RssEntry (
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
    var subtopic: RssSuptopicName? = null,

    @ElementCollection
    @CollectionTable(name = "tags", joinColumns = [JoinColumn(name = "entry_id")])
    @Column(name = "tag", length = 100)
    var tags: List<String> = ArrayList()
)
