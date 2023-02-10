package inforgeon.inforgeon.utils

// например https://habr.com/ru/post/716014/
fun getIdFromHabrUrl(url : String) : Long? {
    val startIndex = url.indexOf("habr.com/ru/post/") + "habr.com/ru/post/".length
    val endIndex = url.indexOf("/", startIndex)
    return url.substring(startIndex, endIndex).toLongOrNull()
}