package com.example.willowwallet

import com.example.willowwallet.utils.DateUtils
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {
    @Test fun `startOfDay returns midnight`() {
        val cal = Calendar.getInstance().apply { set(2025, 0, 15, 14, 30, 0) }
        val result = Calendar.getInstance().apply { timeInMillis = DateUtils.startOfDay(cal.timeInMillis) }
        assertEquals(0, result.get(Calendar.HOUR_OF_DAY))
    }
    @Test fun `endOfDay returns 23 59`() {
        val cal = Calendar.getInstance()
        val result = Calendar.getInstance().apply { timeInMillis = DateUtils.endOfDay(cal.timeInMillis) }
        assertEquals(23, result.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, result.get(Calendar.MINUTE))
    }
    @Test fun `valid time formats pass`() {
        assertTrue(DateUtils.isValidTimeFormat("09:30"))
        assertTrue(DateUtils.isValidTimeFormat("23:59"))
    }
    @Test fun `invalid time formats fail`() {
        assertFalse(DateUtils.isValidTimeFormat("25:00"))
        assertFalse(DateUtils.isValidTimeFormat("abc"))
        assertFalse(DateUtils.isValidTimeFormat(""))
    }
    @Test fun `formatCurrency formats correctly`() = assertTrue(DateUtils.formatCurrency(0.0).contains("0"))
}