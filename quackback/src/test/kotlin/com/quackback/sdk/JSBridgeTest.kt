package com.quackback.sdk
import com.quackback.sdk.internal.JSBridge
import org.junit.Assert.*
import org.junit.Test

class JSBridgeTest {
    @Test fun `init command`() {
        val c = QuackbackConfig(appId = "a", baseURL = "https://x.com", theme = QuackbackTheme.DARK, locale = "fr")
        val js = JSBridge.initCommand(c)
        assertTrue(js.contains("\"appId\":\"a\"")); assertTrue(js.contains("\"theme\":\"dark\""))
    }
    @Test fun `identify SSO`() { assertTrue(JSBridge.identifyCommand(ssoToken = "t").contains("\"ssoToken\":\"t\"")) }
    @Test fun `identify attrs`() {
        val js = JSBridge.identifyCommand(userId = "u", email = "e", name = "n", avatarURL = null)
        assertTrue(js.contains("\"id\":\"u\"")); assertFalse(js.contains("avatarURL"))
    }
    @Test fun `open with board`() { assertTrue(JSBridge.openCommand("bugs").contains("\"board\":\"bugs\"")) }
    @Test fun `open nil`() { assertEquals("Quackback('open');", JSBridge.openCommand(null)) }
    @Test fun `logout`() { assertEquals("Quackback('logout');", JSBridge.logoutCommand()) }
    @Test fun `parse vote`() {
        val p = JSBridge.parseEvent("""{"event":"vote","data":{"type":"quackback:event","payload":{"postId":"p1"}}}""")!!
        assertEquals(QuackbackEvent.VOTE, p.event); assertEquals("p1", p.data["postId"])
    }
    @Test fun `parse invalid`() { assertNull(JSBridge.parseEvent("bad")) }
    @Test fun `init without locale`() {
        val c = QuackbackConfig(appId = "a", baseURL = "https://x.com", theme = QuackbackTheme.LIGHT)
        val js = JSBridge.initCommand(c)
        assertTrue(js.contains("\"theme\":\"light\""))
        assertFalse(js.contains("locale"))
    }
    @Test fun `init system theme`() {
        val c = QuackbackConfig(appId = "a", baseURL = "https://x.com")
        assertTrue(JSBridge.initCommand(c).contains("\"theme\":\"user\""))
    }
    @Test fun `identify attrs with avatarURL`() {
        val js = JSBridge.identifyCommand(userId = "u", email = "e", name = "n", avatarURL = "https://img.com/a.png")
        assertTrue(js.contains("\"avatarURL\":\"https://img.com/a.png\""))
    }
    @Test fun `identify attrs without name`() {
        val js = JSBridge.identifyCommand(userId = "u", email = "e", name = null, avatarURL = null)
        assertTrue(js.contains("\"id\":\"u\""))
        assertFalse(js.contains("\"name\""))
    }
    @Test fun `parse close event`() {
        val p = JSBridge.parseEvent("""{"event":"close","data":{"type":"quackback:close"}}""")!!
        assertEquals(QuackbackEvent.CLOSE, p.event)
    }
    @Test fun `parse submit event`() {
        val p = JSBridge.parseEvent("""{"event":"submit","data":{"type":"quackback:event","payload":{"postId":"p2"}}}""")!!
        assertEquals(QuackbackEvent.SUBMIT, p.event)
        assertEquals("p2", p.data["postId"])
    }
    @Test fun `parse navigate event`() {
        val p = JSBridge.parseEvent("""{"event":"navigate","data":{"type":"quackback:navigate","payload":{"path":"/bugs"}}}""")!!
        assertEquals(QuackbackEvent.NAVIGATE, p.event)
        assertEquals("/bugs", p.data["path"])
    }
    @Test fun `parse empty string`() { assertNull(JSBridge.parseEvent("")) }
    @Test fun `parse empty object`() { assertNull(JSBridge.parseEvent("{}")) }
    @Test fun `parse unknown event`() { assertNull(JSBridge.parseEvent("""{"event":"unknown","data":{}}""")) }
    @Test fun `bridge script contains native bridge setup`() {
        assertTrue(JSBridge.bridgeScript.contains("__quackbackNative"))
        assertTrue(JSBridge.bridgeScript.contains("QuackbackBridge"))
    }
    @Test fun `commands end with semicolon`() {
        val c = QuackbackConfig(appId = "x", baseURL = "https://x.com")
        assertTrue(JSBridge.initCommand(c).endsWith(";"))
        assertTrue(JSBridge.identifyCommand(ssoToken = "t").endsWith(";"))
        assertTrue(JSBridge.openCommand("b").endsWith(";"))
        assertTrue(JSBridge.openCommand(null).endsWith(";"))
        assertTrue(JSBridge.logoutCommand().endsWith(";"))
    }
    @Test fun `commands start with Quackback`() {
        val c = QuackbackConfig(appId = "x", baseURL = "https://x.com")
        assertTrue(JSBridge.initCommand(c).startsWith("Quackback("))
        assertTrue(JSBridge.identifyCommand(ssoToken = "t").startsWith("Quackback("))
        assertTrue(JSBridge.openCommand(null).startsWith("Quackback("))
        assertTrue(JSBridge.logoutCommand().startsWith("Quackback("))
    }
}
