package com.example.willowwallet

import com.example.willowwallet.utils.HashUtils
import org.junit.Assert.*
import org.junit.Test

class HashUtilsTest {
    @Test fun `sha256 returns 64 char string`() = assertEquals(64, HashUtils.sha256("password").length)
    @Test fun `same input gives same hash`() = assertEquals(HashUtils.sha256("test"), HashUtils.sha256("test"))
    @Test fun `different inputs give different hashes`() = assertNotEquals(HashUtils.sha256("abc"), HashUtils.sha256("xyz"))
    @Test fun `output is lowercase hex`() = assertTrue(HashUtils.sha256("hello").matches(Regex("[0-9a-f]{64}")))
}