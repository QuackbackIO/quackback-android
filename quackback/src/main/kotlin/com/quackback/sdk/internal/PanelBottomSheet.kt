package com.quackback.sdk.internal
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class PanelBottomSheet(private val wvManager: QuackbackWebViewManager) : BottomSheetDialogFragment() {
    var onDismissed: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        wvManager.loadIfNeeded(requireContext())
        val layout = FrameLayout(requireContext()).apply { layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) }
        wvManager.webView?.let { (it.parent as? ViewGroup)?.removeView(it); layout.addView(it, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)) }
        return layout
    }

    override fun onStart() {
        super.onStart()
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.let {
            BottomSheetBehavior.from(it).apply { state = BottomSheetBehavior.STATE_EXPANDED; peekHeight = (resources.displayMetrics.heightPixels * 0.5).toInt() }
        }
    }

    override fun onDestroyView() { wvManager.webView?.let { (it.parent as? ViewGroup)?.removeView(it) }; super.onDestroyView() }
    override fun onDestroy() { super.onDestroy(); onDismissed?.invoke() }
}
