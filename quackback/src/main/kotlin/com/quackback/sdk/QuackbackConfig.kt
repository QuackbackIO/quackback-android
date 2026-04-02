package com.quackback.sdk
import android.net.Uri

enum class QuackbackTheme(val value: String) { LIGHT("light"), DARK("dark"), SYSTEM("user") }
enum class QuackbackPosition { BOTTOM_RIGHT, BOTTOM_LEFT }

data class QuackbackConfig(
    val appId: String, val baseURL: String,
    val theme: QuackbackTheme = QuackbackTheme.SYSTEM,
    val position: QuackbackPosition = QuackbackPosition.BOTTOM_RIGHT,
    val buttonColor: String? = null, val locale: String? = null
) {
    val widgetURL: String get() = Uri.parse(baseURL).buildUpon()
        .path("/widget")
        .appendQueryParameter("source", "native")
        .appendQueryParameter("platform", "android")
        .build().toString()
}
