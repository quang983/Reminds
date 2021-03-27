package com.example.reminds

import org.junit.Assert.assertEquals
import org.junit.Test
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


    private fun getTopicToday() {
        val date = Calendar.getInstance()
        date.set(Calendar.HOUR, 0)
        val time1 = date.timeInMillis
        val date2 = Calendar.getInstance()
        date2.set(Calendar.HOUR, 23)
        date2.set(Calendar.MINUTE, 59)
        val time2 = date2.timeInMillis
    }
}
