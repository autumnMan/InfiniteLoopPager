package org.zwh.loop_pager

interface OnPageChangeListener {
    fun onPageSelected(pos: Int)

    fun onPageScrollState(state: Int)
}