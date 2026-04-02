package com.quackback.sdk
import org.junit.Assert.*
import org.junit.Test

class QuackbackConfigTest {
    @Test fun `defaults`() {
        val c = QuackbackConfig(appId = "t", baseURL = "https://x.com")
        assertEquals(QuackbackTheme.SYSTEM, c.theme)
        assertEquals(QuackbackPosition.BOTTOM_RIGHT, c.position)
        assertNull(c.buttonColor)
    }
    @Test fun `widget URL has native params`() {
        val c = QuackbackConfig(appId = "t", baseURL = "https://x.com")
        assertTrue(c.widgetURL.contains("source=native"))
        assertTrue(c.widgetURL.contains("platform=android"))
    }
}
