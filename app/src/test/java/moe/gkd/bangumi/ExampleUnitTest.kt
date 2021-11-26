package moe.gkd.bangumi

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun gmt2utc() {
        val gmt = "Wed, 24 Nov 2021 14:01:17 GMT"
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(
            "E, dd MMM yyyy HH:mm:ss zzz",
            Locale.US
        )
        val parse = LocalDateTime.parse(gmt, formatter)
        val timestamp =
            LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val str = formatter2.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()))
        println(str)

    }
}