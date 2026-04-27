package com.example.willowwallet

import org.junit.Assert.*
import org.junit.Test


class GoalsValidationTest {

    // ── Mirrors GoalsViewModel validation logic

    private fun validate(minGoal: Double, maxGoal: Double): String? = when {
        minGoal < 0.0 || maxGoal < 0.0 -> "Goals cannot be negative."
        maxGoal == 0.0                  -> "Maximum budget must be above R 0.00. Drag the slider to set it."
        maxGoal < minGoal               -> "Maximum goal must be greater than or equal to the minimum goal."
        else                            -> null // success
    }



    @Test
    fun `valid goal returns null (success)`() {
        assertNull(validate(1000.0, 5000.0))
    }

    @Test
    fun `zero max returns error`() {
        assertNotNull(validate(0.0, 0.0))
    }

    @Test
    fun `min greater than max returns error`() {
        assertNotNull(validate(5000.0, 1000.0))
    }

    @Test
    fun `negative min returns error`() {
        assertNotNull(validate(-100.0, 5000.0))
    }

    @Test
    fun `negative max returns error`() {
        assertNotNull(validate(0.0, -500.0))
    }

    @Test
    fun `min equal to max is valid`() {
        // maxGoal >= minGoal is the rule so equal is allowed
        assertNull(validate(2000.0, 2000.0))
    }

    @Test
    fun `zero min with positive max is valid`() {
        assertNull(validate(0.0, 3000.0))
    }

    @Test
    fun `large values are valid`() {
        assertNull(validate(10000.0, 50000.0))
    }

    @Test
    fun `error message for zero max is correct`() {
        val msg = validate(0.0, 0.0)
        assertEquals("Maximum budget must be above R 0.00. Drag the slider to set it.", msg)
    }
}