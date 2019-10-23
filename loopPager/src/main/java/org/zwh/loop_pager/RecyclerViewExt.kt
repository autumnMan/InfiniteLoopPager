package org.zwh.loop_pager

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setLoop(loop: Boolean) {
    (layoutManager as? LoopLayoutManager)?.setLoopEnable(loop)
}

fun RecyclerView.curSelectedPage(): Int {
    return (layoutManager as? LoopLayoutManager)?.curItem ?: -1
}

