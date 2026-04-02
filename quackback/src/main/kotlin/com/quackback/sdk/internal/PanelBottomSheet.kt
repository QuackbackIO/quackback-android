package com.quackback.sdk.internal
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment

internal class PanelBottomSheet(private val wvManager: QuackbackWebViewManager) : DialogFragment() {
    var onDismissed: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                // Disable default window animation — we animate the content view instead
                setWindowAnimations(0)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        wvManager.loadIfNeeded(requireContext())
        val layout = FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        wvManager.webView?.let {
            (it.parent as? ViewGroup)?.removeView(it)
            layout.addView(it, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Slide up from bottom + fade in
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()
        view.translationY = screenHeight * 0.15f
        view.alpha = 0f
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f),
                ObjectAnimator.ofFloat(view, View.ALPHA, 1f)
            )
            duration = 300
            interpolator = DecelerateInterpolator(1.5f)
            start()
        }
    }

    override fun dismiss() {
        val v = view ?: run { super.dismiss(); return }
        // Slide down + fade out, then actually dismiss
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, resources.displayMetrics.heightPixels * 0.15f),
                ObjectAnimator.ofFloat(v, View.ALPHA, 0f)
            )
            duration = 200
            interpolator = DecelerateInterpolator()
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    super@PanelBottomSheet.dismiss()
                }
            })
            start()
        }
    }

    override fun onDestroyView() {
        wvManager.webView?.let { (it.parent as? ViewGroup)?.removeView(it) }
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDismissed?.invoke()
    }
}
