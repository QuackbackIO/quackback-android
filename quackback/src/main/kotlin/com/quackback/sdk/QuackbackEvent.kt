package com.quackback.sdk
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

enum class QuackbackEvent(val value: String) {
    READY("ready"), VOTE("vote"), SUBMIT("submit"), CLOSE("close"), NAVIGATE("navigate");
    companion object { fun fromValue(v: String) = entries.find { it.value == v } }
}

data class EventToken(val id: String = UUID.randomUUID().toString())
typealias EventListener = (Map<String, Any>) -> Unit

internal class EventEmitter {
    private data class Entry(val token: EventToken, val handler: EventListener)
    private val listeners = ConcurrentHashMap<QuackbackEvent, CopyOnWriteArrayList<Entry>>()
    fun on(event: QuackbackEvent, handler: EventListener): EventToken {
        val t = EventToken(); listeners.getOrPut(event) { CopyOnWriteArrayList() }.add(Entry(t, handler)); return t
    }
    fun off(token: EventToken) { for (l in listeners.values) l.removeIf { it.token == token } }
    fun emit(event: QuackbackEvent, data: Map<String, Any>) { listeners[event]?.forEach { it.handler(data) } }
    fun removeAll() { listeners.clear() }
}
