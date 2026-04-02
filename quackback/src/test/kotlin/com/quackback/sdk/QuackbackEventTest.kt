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
}
