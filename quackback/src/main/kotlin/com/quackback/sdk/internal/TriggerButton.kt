package com.quackback.sdk.internal
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.quackback.sdk.QuackbackPosition
import com.quackback.sdk.R

internal class TriggerButton(
    private val activity: Activity, private val position: QuackbackPosition,
    color: String?, private val onClick: () -> Unit
) {
    private var button: FrameLayout? = null
    private var chatIcon: ImageView? = null
    private var closeIcon: ImageView? = null
    private var isOpen = false
    private val dp = activity.resources.displayMetrics.density
    private val sizePx = (48 * dp).toInt()
    private val marginPx = (24 * dp).toInt()
    private val iconPx = (28 * dp).toInt()
    private val bgColor = parseColor(color)

    fun install() {
        if (button != null) return

        val iconParams = FrameLayout.LayoutParams(iconPx, iconPx).apply {
            gravity = Gravity.CENTER
        }

        val chat = ImageView(activity).apply {
            setImageResource(R.drawable.qb_ic_chat)
            scaleType = ImageView.ScaleType.FIT_CENTER
            alpha = 1f
        }
        chatIcon = chat

        val close = ImageView(activity).apply {
            setImageResource(R.drawable.qb_ic_close)
            scaleType = ImageView.ScaleType.FIT_CENTER
            alpha = 0f
            rotation = -90f
        }
        closeIcon = close

        val bg = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(bgColor) }
        val btn = FrameLayout(activity).apply {
            background = bg
            elevation = 6 * dp
            addView(chat, iconParams)
            addView(close, iconParams)
            setOnClickListener { onClick() }
        }

        // Hover-like press effect
        btn.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).start()
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }
            }
            false // let click still fire
        }

        val gravity = Gravity.BOTTOM or if (position == QuackbackPosition.BOTTOM_RIGHT) Gravity.END else Gravity.START
        val params = FrameLayout.LayoutParams(sizePx, sizePx).apply {
            this.gravity = gravity
            setMargins(marginPx, marginPx, marginPx, marginPx)
        }
        (activity.window.decorView as FrameLayout).addView(btn, params)
        button = btn
    }

    fun remove() { button?.let { (it.parent as? FrameLayout)?.removeView(it) }; button = null }

    fun updateColor(hex: String?) {
        val color = parseColor(hex)
        val bg = button?.background as? GradientDrawable ?: return
        bg.setColor(color)
    }

    fun setOpen(open: Boolean) {
        if (open == isOpen) return
        isOpen = open
        val chat = chatIcon ?: return
        val close = closeIcon ?: return
        val duration = 220L
        val overshoot = OvershootInterpolator(1.5f)

        if (open) {
            AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(chat, View.ALPHA, 0f),
                    ObjectAnimator.ofFloat(chat, View.ROTATION, 90f),
                    ObjectAnimator.ofFloat(close, View.ALPHA, 1f),
                    ObjectAnimator.ofFloat(close, View.ROTATION, 0f),
                )
                this.duration = duration
                interpolator = overshoot
                start()
            }
        } else {
            AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(chat, View.ALPHA, 1f),
                    ObjectAnimator.ofFloat(chat, View.ROTATION, 0f),
                    ObjectAnimator.ofFloat(close, View.ALPHA, 0f),
                    ObjectAnimator.ofFloat(close, View.ROTATION, -90f),
                )
                this.duration = duration
                interpolator = overshoot
                start()
            }
        }
    }

    private fun parseColor(hex: String?): Int =
        if (hex != null && hex.startsWith("#") && hex.length == 7)
            try { Color.parseColor(hex) } catch (_: Exception) { Color.parseColor("#6366f1") }
        else Color.parseColor("#6366f1")
}
