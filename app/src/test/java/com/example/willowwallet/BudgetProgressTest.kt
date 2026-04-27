package com.example.willowwallet

import org.junit.Assert.*
import org.junit.Test

class BudgetProgressTest {

    // ── Mirrors HomeViewModel logic

    private fun calcProgressPct(totalSpent: Double, maxGoal: Double): Int {
        if (maxGoal <= 0.0) return 0
        return ((totalSpent / maxGoal) * 100.0).toInt().coerceIn(0, 100)
    }

    private fun budgetStatus(totalSpent: Double, minGoal: Double, maxGoal: Double): String = when {
        totalSpent > maxGoal  -> "over"
        totalSpent >= minGoal -> "on_track"
        else                  -> "under"
    }

    // ── calcProgressPct tests

    @Test
    fun `zero max goal returns 0 percent`() {
        assertEquals(0, calcProgressPct(500.0, 0.0))
    }

    @Test
    fun `half spent returns 50 percent`() {
        assertEquals(50, calcProgressPct(500.0, 1000.0))
    }

    @Test
    fun `fully spent returns 100 percent`() {
        assertEquals(100, calcProgressPct(1000.0, 1000.0))
    }

    @Test
    fun `overspending is clamped to 100`() {
        assertEquals(100, calcProgressPct(2000.0, 1000.0))
    }

    @Test
    fun `nothing spent returns 0`() {
        assertEquals(0, calcProgressPct(0.0, 5000.0))
    }

    // ── budgetStatus tests ────────────────────────────────────────────────────

    @Test
    fun `over max returns over`() {
        assertEquals("over", budgetStatus(6000.0, 1000.0, 5000.0))
    }

    @Test
    fun `between min and max returns on_track`() {
        assertEquals("on_track", budgetStatus(3000.0, 1000.0, 5000.0))
    }

    @Test
    fun `exactly at min returns on_track`() {
        assertEquals("on_track", budgetStatus(1000.0, 1000.0, 5000.0))
    }

    @Test
    fun `below min returns under`() {
        assertEquals("under", budgetStatus(500.0, 1000.0, 5000.0))
    }

    @Test
    fun `zero spent is under`() {
        assertEquals("under", budgetStatus(0.0, 1000.0, 5000.0))
    }

    @Test
    fun `exactly at max returns on_track not over`() {
        assertEquals("on_track", budgetStatus(5000.0, 1000.0, 5000.0))
    }

    @Test
    fun `one cent over max returns over`() {
        assertEquals("over", budgetStatus(5000.01, 1000.0, 5000.0))
    }
}