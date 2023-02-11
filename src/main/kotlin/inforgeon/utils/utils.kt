package inforgeon.inforgeon.utils

import inforgeon.inforgeon.constant.RssTopicName

// например https://habr.com/ru/post/716014/
fun getIdFromHabrUrl(url : String, topicName: RssTopicName) : Long? {
    val startIndex = url.indexOf("habr.com/ru/post/") + "habr.com/ru/post/".length
    val endIndex = url.indexOf("/", startIndex)
    return "${topicName.ordinal+1}${url.substring(startIndex, endIndex)}".toLongOrNull()
}