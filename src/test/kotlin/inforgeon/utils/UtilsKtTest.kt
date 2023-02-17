package inforgeon.utils

import inforgeon.inforgeon.constant.RssTopicName.JAVA
import inforgeon.inforgeon.utils.getIdFromHabrUrl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class UtilsKtTest {

    @Test
    fun getIdFromHabrUrlTest() {

        val url = "habr.com/ru/post/123/"
        val topicName = JAVA

        Assertions.assertEquals(1123, getIdFromHabrUrl(url, topicName))

    }

    @Test
    fun getIdFromHabrUrlTest2() {

        val url = "habr.com/ru/post/abc/"
        val topicName = JAVA

        Assertions.assertNull(getIdFromHabrUrl(url, topicName))

    }

}