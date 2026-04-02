package com.quackback.sdk.internal
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.*
import com.quackback.sdk.QuackbackConfig
import com.quackback.sdk.QuackbackEvent

internal interface WebViewEventListener { fun onEvent(event: QuackbackEvent, data: Map<String, Any>); fun onReady() }

@SuppressLint("SetJavaScriptEnabled")
internal class QuackbackWebViewManager(private val config: QuackbackConfig) {
    var webView: WebView? = null; private set
    var listener: WebViewEventListener? = null
    private var isReady = false
    private val pending = mutableListOf<String>()

    fun loadIfNeeded(ctx: Context) {
        if (webView != null) return
        val wv = WebView(ctx).apply {
            settings.javaScriptEnabled = true; settings.domStorageEnabled = true
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(v: WebView?, u: String?, f: android.graphics.Bitmap?) { v?.evaluateJavascript(JSBridge.bridgeScript, null) }
                override fun shouldOverrideUrlLoading(v: WebView?, r: WebResourceRequest?): Boolean {
                    val url = r?.url ?: return false
                    if (url.host != Uri.parse(config.baseURL).host) { ctx.startActivity(Intent(Intent.ACTION_VIEW, url)); return true }
                    return false
                }
            }
            addJavascriptInterface(Bridge(), "QuackbackBridge")
        }
        wv.loadUrl(config.widgetURL); webView = wv
    }

    fun execute(js: String) { if (!isReady) { pending.add(js); return }; webView?.post { webView?.evaluateJavascript(js, null) } }
    fun tearDown() { webView?.removeJavascriptInterface("QuackbackBridge"); webView?.stopLoading(); webView?.destroy(); webView = null; isReady = false; pending.clear() }

    private inner class Bridge {
        @JavascriptInterface fun onEvent(json: String) {
            val p = JSBridge.parseEvent(json) ?: return
            if (p.event == QuackbackEvent.READY) {
                isReady = true
                webView?.post { webView?.evaluateJavascript(JSBridge.initCommand(config), null) }
                pending.forEach { cmd -> webView?.post { webView?.evaluateJavascript(cmd, null) } }
                pending.clear(); listener?.onReady(); return
            }
            listener?.onEvent(p.event, p.data)
        }
    }
}
