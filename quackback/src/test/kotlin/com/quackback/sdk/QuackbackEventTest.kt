package com.quackback.sdk
import org.junit.Assert.*
import org.junit.Test

class EventEmitterTest {
    @Test fun `fires listener`() {
        val e = EventEmitter()
        var got: Map<String, Any>? = null
        e.on(QuackbackEvent.VOTE) { got = it }
        e.emit(QuackbackEvent.VOTE, mapOf("postId" to "p1"))
        assertEquals("p1", got!!["postId"])
    }
    @Test fun `removes by token`() {
        val e = EventEmitter()
        var n = 0
        val tok = e.on(QuackbackEvent.SUBMIT) { n++ }
        e.emit(QuackbackEvent.SUBMIT, emptyMap()); assertEquals(1, n)
        e.off(tok)
        e.emit(QuackbackEvent.SUBMIT, emptyMap()); assertEquals(1, n)
    }
    @Test fun `removeAll clears all`() {
        val e = EventEmitter()
        var n = 0
        e.on(QuackbackEvent.VOTE) { n++ }
        e.on(QuackbackEvent.SUBMIT) { n++ }
        e.emit(QuackbackEvent.VOTE, emptyMap())
        e.emit(QuackbackEvent.SUBMIT, emptyMap())
        assertEquals(2, n)
        e.removeAll()
        e.emit(QuackbackEvent.VOTE, emptyMap())
        e.emit(QuackbackEvent.SUBMIT, emptyMap())
        assertEquals(2, n)
    }
    @Test fun `multiple listeners same event`() {
        val e = EventEmitter()
        var c1 = 0; var c2 = 0
        e.on(QuackbackEvent.VOTE) { c1++ }
        e.on(QuackbackEvent.VOTE) { c2++ }
        e.emit(QuackbackEvent.VOTE, emptyMap())
        assertEquals(1, c1); assertEquals(1, c2)
    }
    @Test fun `off non-existent token is safe`() {
        val e = EventEmitter()
        var n = 0
        e.on(QuackbackEvent.VOTE) { n++ }
        e.off(EventToken("nonexistent"))
        e.emit(QuackbackEvent.VOTE, emptyMap())
        assertEquals(1, n)
    }
    @Test fun `emit with no listeners is safe`() {
        val e = EventEmitter()
        e.emit(QuackbackEvent.VOTE, mapOf("key" to "value")) // should not crash
    }
    @Test fun `remove only target listener`() {
        val e = EventEmitter()
        var c1 = 0; var c2 = 0
        e.on(QuackbackEvent.VOTE) { c1++ }
        val tok2 = e.on(QuackbackEvent.VOTE) { c2++ }
        e.off(tok2)
        e.emit(QuackbackEvent.VOTE, emptyMap())
        assertEquals(1, c1); assertEquals(0, c2)
    }
    @Test fun `all event types have correct values`() {
        assertEquals("ready", QuackbackEvent.READY.value)
        assertEquals("vote", QuackbackEvent.VOTE.value)
        assertEquals("submit", QuackbackEvent.SUBMIT.value)
        assertEquals("close", QuackbackEvent.CLOSE.value)
        assertEquals("navigate", QuackbackEvent.NAVIGATE.value)
    }
    @Test fun `fromValue returns correct enum`() {
        assertEquals(QuackbackEvent.VOTE, QuackbackEvent.fromValue("vote"))
        assertEquals(QuackbackEvent.READY, QuackbackEvent.fromValue("ready"))
        assertNull(QuackbackEvent.fromValue("nonexistent"))
    }
}
