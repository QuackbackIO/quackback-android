package com.quackback.sdk.internal
import android.app.Dialog
import android.os.Bundle
import android.view.*
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
                setWindowAnimations(android.R.style.Animation_Activity)
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

    override fun onDestroyView() {
        wvManager.webView?.let { (it.parent as? ViewGroup)?.removeView(it) }
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDismissed?.invoke()
    }
}
