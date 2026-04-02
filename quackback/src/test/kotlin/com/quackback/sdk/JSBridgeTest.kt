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
}
