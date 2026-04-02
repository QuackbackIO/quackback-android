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
    @Test fun `custom values preserved`() {
        val c = QuackbackConfig(
            appId = "my-app", baseURL = "https://fb.example.com",
            theme = QuackbackTheme.DARK, position = QuackbackPosition.BOTTOM_LEFT,
            buttonColor = "#FF0000", locale = "fr"
        )
        assertEquals("my-app", c.appId)
        assertEquals(QuackbackTheme.DARK, c.theme)
        assertEquals(QuackbackPosition.BOTTOM_LEFT, c.position)
        assertEquals("#FF0000", c.buttonColor)
        assertEquals("fr", c.locale)
    }
    @Test fun `theme raw values`() {
        assertEquals("light", QuackbackTheme.LIGHT.value)
        assertEquals("dark", QuackbackTheme.DARK.value)
        assertEquals("user", QuackbackTheme.SYSTEM.value)
    }
    @Test fun `widget URL contains path`() {
        val c = QuackbackConfig(appId = "t", baseURL = "https://custom.domain.com")
        assertTrue(c.widgetURL.contains("/widget"))
        assertTrue(c.widgetURL.contains("custom.domain.com"))
    }
}
