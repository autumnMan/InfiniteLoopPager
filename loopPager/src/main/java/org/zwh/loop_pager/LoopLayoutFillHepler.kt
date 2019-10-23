package org.zwh.loop_pager

import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

internal fun LoopLayoutManager.fill(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
    val move2Left = dx > 0
    return if (move2Left) {
        // 左移，填充右边的item
        fillRightWhenScroll2Left(dx, recycler, state)
    } else {
        // 右移，填充左边的item
        fillLeftWhenScroll2Right(dx, recycler, state)
    }
}

/**
 * 左移 dx > 0，需要填充右边界
 */
internal fun LoopLayoutManager.fillRightWhenScroll2Left(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
    val childCount = childCount
    if (childCount == 0) {
        return 0
    }
    val lastView = getChildAt(childCount - 1) ?: return 0
    var pos = getPosition(lastView)
    if (pos == RecyclerView.NO_POSITION) {
        return 0
    }

    val orientationHelper = getOrientationHelper()
    var viewEnd = orientationHelper.getDecoratedEnd(lastView)
    val parentEnd = orientationHelper.endAfterPadding
    if (viewEnd - parentEnd >= dx) {
        // 最后一个item有足够的空间移动dx
        return dx
    }
    val canLoop = canLoop()
    while (viewEnd - parentEnd < dx) {
        val isLastPos = pos == itemCount - 1
        if (!isLastPos) {
            ++pos
        } else {
            // 右边已经是最后一个item了
            if (canLoop) {
                // 支持循环
                pos = 0
            } else {
                // 不支持循环，则滑动剩余的空间
                return viewEnd - parentEnd
            }
        }

        val scrapView = recycler.getViewForPosition(pos) ?: return 0
        addView(scrapView)
        measureChildWithMargins(scrapView, 0, 0)
        val scrapViewW = getDecoratedMeasuredWidth(scrapView)
        val scrapViewH = getDecoratedMeasuredHeight(scrapView)
        layoutDecoratedWithMargins(scrapView, viewEnd, 0, viewEnd + scrapViewW, scrapViewH)
        viewEnd += scrapViewW
    }
    return dx
}

/**
 * 右移 dx < 0，需要填充左边界
 */
internal fun LoopLayoutManager.fillLeftWhenScroll2Right(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
    val childCount = childCount
    if (childCount == 0) {
        return 0
    }
    val firstView = getChildAt(0) ?: return 0
    var pos = getPosition(firstView)
    if (pos == RecyclerView.NO_POSITION) {
        return 0
    }

    val orientationHelper = getOrientationHelper()
    var viewStart = orientationHelper.getDecoratedStart(firstView)
    val parentStart = orientationHelper.startAfterPadding
    if (abs(viewStart - parentStart) >= abs(dx)) {
        // 第一个item有足够的空间移动dx
        return dx
    }
    val canLoop = canLoop()
    while (abs(viewStart - parentStart) < abs(dx)) {
        val isFirstPos = pos == 0
        if (!isFirstPos) {
            --pos
        } else {
            // 左边已经是第一个item了
            if (canLoop) {
                // 支持循环
                pos = itemCount - 1
            } else {
                // 不支持循环，则滑动剩余的空间
                return viewStart - parentStart
            }
        }

        val scrapView = recycler.getViewForPosition(pos) ?: return 0
        addView(scrapView, 0)
        measureChildWithMargins(scrapView, 0, 0)
        val scrapViewW = getDecoratedMeasuredWidth(scrapView)
        val scrapViewH = getDecoratedMeasuredHeight(scrapView)
        layoutDecoratedWithMargins(scrapView, viewStart - scrapViewW, 0, viewStart, scrapViewH)
        viewStart -= scrapViewW
    }
    return dx
}

internal fun LoopLayoutManager.recycleViews(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) {
    val childCount = childCount
    if (childCount == 0) {
        return
    }
    val orientationHelper = getOrientationHelper()
    val parentStart = orientationHelper.startAfterPadding
    val parentEnd = orientationHelper.endAfterPadding
    for (i in (childCount - 1) downTo 0) {
        val child = getChildAt(i) ?: continue
        val viewStart = orientationHelper.getDecoratedStart(child)
        val viewEnd = orientationHelper.getDecoratedEnd(child)
        // dx > 0 左移， dx < 0 右移
        if ((dx > 0 && viewEnd < parentStart) || (dx < 0 && viewStart > parentEnd)) {
            removeAndRecycleView(child, recycler)
        }
    }
}

internal fun LoopLayoutManager.getOrientationHelper(): OrientationHelper {
    if (orientationHelper == null) {
        orientationHelper = OrientationHelper.createHorizontalHelper(this)
    }
    return orientationHelper!!
}

internal fun LoopLayoutManager.canLoop(): Boolean {
    return itemCount > 1 && loop
}








