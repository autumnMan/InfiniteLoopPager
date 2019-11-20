package org.zwh.loop_pager

import androidx.recyclerview.widget.RecyclerView

internal fun LoopLayoutManager.handleScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
    if (RecyclerView.SCROLL_STATE_IDLE == newState && childCount > 0) {
        val v = snapHelper.findSnapView(this) ?: return
        val pos = getPosition(v)
        android.util.Log.d(TAG, "state idle, cur selected pos is : $pos")
        if (pos >= 0 && curItem != pos) {
            curItem = pos
            dispatchOnPageSelected(curItem)
        }
    }
}

internal fun LoopLayoutManager.handleScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

}