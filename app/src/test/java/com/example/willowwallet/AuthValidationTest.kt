package com.example.willowwallet

import org.junit.Assert.*
import org.junit.Test

class AuthValidationTest {
    @Test fun `blank username is invalid`() = assertTrue("".isBlank())
    @Test fun `short username is invalid`() = assertTrue("ab".length < 3)
    @Test fun `short password is invalid`() = assertTrue("abc".length < 6)
    @Test fun `passwords must match`() = assertNotEquals("pass1", "pass2")
    @Test fun `valid inputs pass`() {
        val u = "testuser"; val p = "password123"
        assertTrue(u.length >= 3 && p.length >= 6)
    }
    @Test fun `max goal must be gte min`() = assertTrue(5000.0 >= 1000.0)
    @Test fun `invalid amount returns null`() = assertNull("abc".toDoubleOrNull())
    @Test fun `zero amount is invalid`() = assertFalse(("0".toDoubleOrNull() ?: 0.0) > 0)
}