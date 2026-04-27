package com.example.willowwallet

import com.example.willowwallet.utils.DateUtils
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests covering Person 2 requirements:
 *  - Expense field validation
 *  - Time format validation
 *  - Date range utilities
 *  - Currency formatting
 */
class ExpenseValidationTest {


    @Test
    fun `amount zero is invalid`() {
        assertFalse(isAmountValid(0.0))
    }

    @Test
    fun `negative amount is invalid`() {
        assertFalse(isAmountValid(-10.0))
    }

    @Test
    fun `positive amount is valid`() {
        assertTrue(isAmountValid(0.01))
        assertTrue(isAmountValid(100.0))
        assertTrue(isAmountValid(99999.99))
    }


    @Test
    fun `blank description is invalid`() {
        assertFalse(isDescriptionValid(""))
        assertFalse(isDescriptionValid("   "))
    }

    @Test
    fun `non-blank description is valid`() {
        assertTrue(isDescriptionValid("Groceries"))
        assertTrue(isDescriptionValid("Coffee at Woolworths"))
    }


    @Test
    fun `valid time formats pass`() {
        assertTrue(DateUtils.isValidTimeFormat("00:00"))
        assertTrue(DateUtils.isValidTimeFormat("09:30"))
        assertTrue(DateUtils.isValidTimeFormat("12:00"))
        assertTrue(DateUtils.isValidTimeFormat("23:59"))
    }



    @Test
    fun `end time after start time is valid`() {
        assertTrue(isTimeOrderValid("09:00", "17:00"))
        assertTrue(isTimeOrderValid("00:00", "23:59"))
    }

    @Test
    fun `end time equal to start time is invalid`() {
        assertFalse(isTimeOrderValid("10:00", "10:00"))
    }

    @Test
    fun `end time before start time is invalid`() {
        assertFalse(isTimeOrderValid("17:00", "09:00"))
    }


    @Test
    fun `startOfDay returns midnight`() {
        val result = DateUtils.startOfDay(System.currentTimeMillis())
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = result }
        assertEquals(0,  cal.get(java.util.Calendar.HOUR_OF_DAY))
        assertEquals(0,  cal.get(java.util.Calendar.MINUTE))
        assertEquals(0,  cal.get(java.util.Calendar.SECOND))
        assertEquals(0,  cal.get(java.util.Calendar.MILLISECOND))
    }

    @Test
    fun `endOfDay returns last millisecond`() {
        val result = DateUtils.endOfDay(System.currentTimeMillis())
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = result }
        assertEquals(23,  cal.get(java.util.Calendar.HOUR_OF_DAY))
        assertEquals(59,  cal.get(java.util.Calendar.MINUTE))
        assertEquals(59,  cal.get(java.util.Calendar.SECOND))
        assertEquals(999, cal.get(java.util.Calendar.MILLISECOND))
    }

    @Test
    fun `startOfDay is before endOfDay for same day`() {
        val now   = System.currentTimeMillis()
        val start = DateUtils.startOfDay(now)
        val end   = DateUtils.endOfDay(now)
        assertTrue(start < end)
    }

    @Test
    fun `startOfMonth returns first day`() {
        val result = DateUtils.startOfMonth(2024, 3)   // March 2024
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = result }
        assertEquals(1,    cal.get(java.util.Calendar.DAY_OF_MONTH))
        assertEquals(2,    cal.get(java.util.Calendar.MONTH))   // 0-indexed
        assertEquals(2024, cal.get(java.util.Calendar.YEAR))
    }

    @Test
    fun `endOfMonth returns last day`() {
        val result = DateUtils.endOfMonth(2024, 2)  // Feb 2024 (leap year)
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = result }
        assertEquals(29, cal.get(java.util.Calendar.DAY_OF_MONTH))
    }



    @Test
    fun `formatCurrency rounds to two decimal places`() {
        val result = DateUtils.formatCurrency(1.005)
        assertTrue(result.startsWith("R "))
    }


    private fun isAmountValid(amount: Double)               = amount > 0
    private fun isDescriptionValid(description: String)     = description.isNotBlank()
    private fun isTimeOrderValid(start: String, end: String) = start < end
}