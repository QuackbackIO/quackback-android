package com.quackback.sdk.internal
import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.quackback.sdk.QuackbackPosition

internal class TriggerButton(
    private val activity: Activity, private val position: QuackbackPosition,
    color: String?, private val onClick: () -> Unit
) {
    private var button: FrameLayout? = null
    private var isOpen = false
    private val dp = activity.resources.displayMetrics.density
    private val sizePx = (48 * dp).toInt()
    private val marginPx = (16 * dp).toInt()
    private val bgColor = parseColor(color)

    fun install() {
        if (button != null) return
        val icon = ImageView(activity).apply { setImageResource(android.R.drawable.ic_dialog_info); scaleType = ImageView.ScaleType.CENTER_INSIDE }
        val bg = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(bgColor) }
        val btn = FrameLayout(activity).apply {
            background = bg; elevation = 6 * dp
            addView(icon, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
            setOnClickListener { onClick() }
        }
        val gravity = Gravity.BOTTOM or if (position == QuackbackPosition.BOTTOM_RIGHT) Gravity.END else Gravity.START
        val params = FrameLayout.LayoutParams(sizePx, sizePx).apply { this.gravity = gravity; setMargins(marginPx, marginPx, marginPx, marginPx) }
        (activity.window.decorView as FrameLayout).addView(btn, params); button = btn
    }

    fun remove() { button?.let { (it.parent as? FrameLayout)?.removeView(it) }; button = null }
    fun setOpen(open: Boolean) { if (open == isOpen) return; isOpen = open; button?.let { ObjectAnimator.ofFloat(it, View.ROTATION, if (open) 45f else 0f).apply { duration = 250 }.start() } }
    private fun parseColor(hex: String?): Int = if (hex != null && hex.startsWith("#") && hex.length == 7) try { Color.parseColor(hex) } catch (_: Exception) { Color.parseColor("#2563EB") } else Color.parseColor("#2563EB")
}
