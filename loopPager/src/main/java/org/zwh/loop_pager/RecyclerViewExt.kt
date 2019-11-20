package org.zwh.loop_pager

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setLoop(loop: Boolean) {
    (layoutManager as? LoopLayoutManager)?.setLoopEnable(loop)
}

fun RecyclerView.curSelectedPage(): Int {
    return (layoutManager as? LoopLayoutManager)?.curItem ?: -1
}

fun RecyclerView.addOnPageChangeListener(l: OnPageChangeListener) {
    (layoutManager as? LoopLayoutManager)?.addPageChangeListener(l)
}

fun RecyclerView.removeOnPageChangeListener(l: OnPageChangeListener) {
    (layoutManager as? LoopLayoutManager)?.removePageChangeListener(l)
}