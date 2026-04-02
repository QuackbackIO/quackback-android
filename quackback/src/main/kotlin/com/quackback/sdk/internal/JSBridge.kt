package com.quackback.sdk.internal
import com.quackback.sdk.QuackbackConfig
import com.quackback.sdk.QuackbackEvent
import org.json.JSONObject

internal data class ParsedEvent(val event: QuackbackEvent, val data: Map<String, Any>)

internal object JSBridge {
    fun initCommand(config: QuackbackConfig): String {
        val p = JSONObject().apply { put("appId", config.appId); put("theme", config.theme.value); config.locale?.let { put("locale", it) } }
        return "window.postMessage({type:'quackback:init',data:$p},'*');"
    }
    fun identifyCommand(ssoToken: String): String {
        val p = JSONObject().apply { put("ssoToken", ssoToken) }
        return "window.postMessage({type:'quackback:identify',data:$p},'*');"
    }
    fun identifyCommand(userId: String, email: String, name: String?, avatarURL: String?): String {
        val p = JSONObject().apply { put("id", userId); put("email", email); name?.let { put("name", it) }; avatarURL?.let { put("avatarURL", it) } }
        return "window.postMessage({type:'quackback:identify',data:$p},'*');"
    }
    fun openCommand(board: String?): String {
        if (board != null) {
            val p = JSONObject().apply { put("board", board) }
            return "window.postMessage({type:'quackback:open',data:$p},'*');"
        }
        return "window.postMessage({type:'quackback:open'},'*');"
    }
    fun logoutCommand() = "window.postMessage({type:'quackback:identify',data:null},'*');"
    fun parseEvent(json: String): ParsedEvent? {
        return try {
            val obj = JSONObject(json)
            val event = QuackbackEvent.fromValue(obj.optString("event")) ?: return null
            val data = mutableMapOf<String, Any>()
            obj.optJSONObject("data")?.let { d ->
                val src = d.optJSONObject("payload") ?: d
                for (k in src.keys()) data[k] = src.get(k)
            }
            ParsedEvent(event, data)
        } catch (_: Exception) { null }
    }

    val bridgeScript = """
        (function(){
          var dispatch=function(e,d){var m=JSON.stringify({event:e,data:d});QuackbackBridge.onEvent(m);};
          window.__quackbackNative={dispatch:dispatch};
        })();
    """.trimIndent()
}
