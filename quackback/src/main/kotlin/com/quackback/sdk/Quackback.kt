package com.quackback.sdk
import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.quackback.sdk.internal.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object Quackback {
    private var config: QuackbackConfig? = null
    private var wvManager: QuackbackWebViewManager? = null
    private var trigger: TriggerButton? = null
    private var panel: PanelBottomSheet? = null
    private val emitter = EventEmitter()
    private var isShowing = false
    private var pendingIdentify: String? = null
    private var currentActivity: Activity? = null
    private var serverTheme: ServerTheme? = null

    private data class ServerTheme(val lightPrimary: String, val darkPrimary: String)

    private val lifecycle = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(a: Activity) { currentActivity = a }
        override fun onActivityPaused(a: Activity) { if (currentActivity == a) currentActivity = null }
        override fun onActivityCreated(a: Activity, s: Bundle?) {}
        override fun onActivityStarted(a: Activity) {}
        override fun onActivityStopped(a: Activity) {}
        override fun onActivitySaveInstanceState(a: Activity, s: Bundle) {}
        override fun onActivityDestroyed(a: Activity) {}
    }

    private val wvListener = object : WebViewEventListener {
        override fun onEvent(event: QuackbackEvent, data: Map<String, Any>) { if (event == QuackbackEvent.CLOSE) close(); emitter.emit(event, data) }
        override fun onReady() { pendingIdentify?.let { wvManager?.execute(it); pendingIdentify = null } }
    }

    fun configure(context: android.content.Context, config: QuackbackConfig) {
        this.config = config
        (context.applicationContext as? Application)?.registerActivityLifecycleCallbacks(lifecycle)
        fetchTheme(config.baseURL)
    }

    fun identify(ssoToken: String) { enqueue(JSBridge.identifyCommand(ssoToken = ssoToken)) }
    fun identify(userId: String, email: String, name: String? = null, avatarURL: String? = null) { enqueue(JSBridge.identifyCommand(userId, email, name, avatarURL)) }
    fun logout() { enqueue(JSBridge.logoutCommand()) }

    fun open(board: String? = null) {
        val cfg = config ?: return
        val act = currentActivity ?: return
        ensureWV(cfg); wvManager?.execute(JSBridge.openCommand(board)); present(act)
    }

    fun close() { dismiss() }

    fun showTrigger() {
        val cfg = config ?: return
        val act = currentActivity ?: return
        if (trigger != null) return
        val color = cfg.buttonColor ?: resolveThemeColor()
        trigger = TriggerButton(act, cfg.position, color) { if (isShowing) close() else open() }.also { it.install() }
    }

    fun hideTrigger() { trigger?.remove(); trigger = null }
    fun on(event: QuackbackEvent, handler: EventListener) = emitter.on(event, handler)
    fun off(token: EventToken) { emitter.off(token) }

    fun destroy() {
        dismiss(); hideTrigger(); wvManager?.tearDown(); wvManager = null
        emitter.removeAll(); config = null; pendingIdentify = null; serverTheme = null
        (currentActivity?.applicationContext as? Application)?.unregisterActivityLifecycleCallbacks(lifecycle)
        currentActivity = null
    }

    private fun resolveThemeColor(): String? {
        val theme = serverTheme ?: return null
        val cfg = config ?: return theme.lightPrimary
        return when (cfg.theme) {
            QuackbackTheme.LIGHT -> theme.lightPrimary
            QuackbackTheme.DARK -> theme.darkPrimary
            QuackbackTheme.SYSTEM -> theme.lightPrimary // TriggerButton uses this as default
        }
    }

    private fun fetchTheme(baseURL: String) {
        Thread {
            try {
                val url = URL("$baseURL/api/widget/config.json")
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.requestMethod = "GET"
                if (conn.responseCode == 200) {
                    val body = conn.inputStream.bufferedReader().readText()
                    val json = JSONObject(body)
                    val t = json.optJSONObject("theme")
                    if (t != null) {
                        serverTheme = ServerTheme(
                            lightPrimary = t.optString("lightPrimary", "#6366f1"),
                            darkPrimary = t.optString("darkPrimary", "#6366f1"),
                        )
                        // Update trigger if already showing
                        currentActivity?.runOnUiThread {
                            trigger?.updateColor(resolveThemeColor())
                        }
                    }
                }
                conn.disconnect()
            } catch (_: Exception) {
                // Network error — use defaults
            }
        }.start()
    }

    private fun ensureWV(cfg: QuackbackConfig) { if (wvManager != null) return; wvManager = QuackbackWebViewManager(cfg).also { it.listener = wvListener } }
    private fun enqueue(js: String) { if (wvManager?.webView != null) wvManager?.execute(js) else pendingIdentify = js }

    private fun present(act: Activity) {
        if (isShowing) return; val m = wvManager ?: return; val fa = act as? FragmentActivity ?: return; m.loadIfNeeded(act)
        val sheet = PanelBottomSheet(m).also { it.onDismissed = { isShowing = false; trigger?.setOpen(false); panel = null } }
        sheet.show(fa.supportFragmentManager, "quackback"); isShowing = true; trigger?.setOpen(true); panel = sheet
    }

    private fun dismiss() { panel?.dismiss(); panel = null; isShowing = false; trigger?.setOpen(false) }
}
