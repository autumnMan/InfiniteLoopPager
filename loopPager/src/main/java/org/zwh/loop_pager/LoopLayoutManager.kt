package org.zwh.loop_pager

import android.content.Context
import android.graphics.PointF
import androidx.recyclerview.widget.*
import kotlin.math.abs

class LoopLayoutManager(
        private val context: Context
) : RecyclerView.LayoutManager(), RecyclerView.SmoothScroller.ScrollVectorProvider {
    internal val TAG = "LoopLayoutManager"

    private val START = -1
    private val END = 1

    @JvmField
    internal var orientationHelper: OrientationHelper? = null
    private var smoothScroller: RecyclerView.SmoothScroller? = null
    private val onPageChangeListeners = ArrayList<OnPageChangeListener>()

    private var recyclerView: RecyclerView? = null
    internal var loop: Boolean = false
    internal var curItem: Int = -1

    private var requestLayout = false
    private var firstLayout = true

    fun setLoopEnable(loop: Boolean) {
        this.loop = loop
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        if (recyclerView != view) {
            recyclerView = view
            snapHelper.attachToRecyclerView(view)
            view.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    handleScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    handleScrolled(recyclerView, dx, dy)
                }
            })
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        val itemCount = state.itemCount
        if (itemCount <= 0) {
            removeAndRecycleAllViews(recycler)
            return
        }
        if (state.isPreLayout) {
            return
        }
        if (itemCount != 0 && !state.didStructureChange()) {
            // data set not change
            if (!requestLayout) {
                return
            }
        }

        detachAndScrapAttachedViews(recycler)
        val orientationHelper = getOrientationHelper()
        val parentEnd = orientationHelper.endAfterPadding
        var childStart = 0
        val initPos = if (curItem in 0 until itemCount) curItem else 0
        for (i in initPos until itemCount) {
            val v = recycler.getViewForPosition(i)
            addView(v)
            measureChildWithMargins(v, 0, 0)
            val childW = getDecoratedMeasuredWidth(v)
            val childH = getDecoratedMeasuredHeight(v)
            layoutDecoratedWithMargins(v, childStart, 0, childStart + childW, childH)

            childStart += childW
            if (childStart > parentEnd) {
                break
            }
        }

        if (requestLayout || firstLayout) {
            curItem = initPos
        }
        requestLayout = false

        if (firstLayout) {
            firstLayout = false
            dispatchOnPageSelected(initPos)
        }
    }

    private fun normalizedPos(position: Int): Int {
        val itemCount = itemCount
        return if (canLoop()) {
            position % itemCount
        } else {
            if (position >= itemCount) itemCount - 1 else position
        }
    }

    // scroll methods start
    override fun scrollToPosition(position: Int) {
        curItem = normalizedPos(position)
        requestLayout = true
        requestLayout()
        dispatchOnPageSelected(curItem)
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
        val scroller = LinearSmoothScroller(recyclerView.context)
        scroller.targetPosition = normalizedPos(position)
        startSmoothScroll(scroller)
        smoothScroller = scroller
        curItem = scroller.targetPosition
        dispatchOnPageSelected(curItem)
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        val itemCount = itemCount
        if (childCount == 0 || itemCount == 0) {
            return null
        }

        val scroller = smoothScroller
        val firstChildPos = getPosition(getChildAt(0)!!)
        if (canLoop() && scroller != null && (scroller.isRunning || scroller.isPendingInitialRun)) {
            val distancePos = targetPosition - firstChildPos
            val direction = if (targetPosition < firstChildPos) {
                // start
                if (itemCount > 2 * abs(distancePos)) START else END
            } else {
                // end
                if (itemCount > 2 * abs(distancePos)) END else START
            }
//            android.util.Log.d(TAG, "from: $firstChildPos, to: $targetPosition, direction: $direction")
            return PointF(direction.toFloat(), 0f)
        }
        val direction = if (targetPosition < firstChildPos) START else END
        return PointF(direction.toFloat(), 0f)
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (dx == 0) {
            return 0
        }
        val consumedDx = fill(dx, recycler, state)
        if (consumedDx == 0) {
            // 没有消耗dx，说明已经无法移动了，到达边界或者是其它情况
            return 0
        }
        offsetChildrenHorizontal(-consumedDx)
        // 回收不可见的view
        recycleViews(dx, recycler, state)
        return consumedDx
    }

    internal val snapHelper: SnapHelper = object : PagerSnapHelper() {
        override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
            val itemCount = layoutManager.itemCount
            val pos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
//            android.util.Log.d(TAG, "target snap pos: $pos, itemCount: $itemCount")
            if (pos >= itemCount) {
                return 0
            }
            return pos
        }
    }
    // scroll methods end

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        return lp is RecyclerView.LayoutParams
    }

    fun addPageChangeListener(l: OnPageChangeListener) {
        if (!onPageChangeListeners.contains(l)) {
            onPageChangeListeners.add(l)
        }
    }

    fun removePageChangeListener(l: OnPageChangeListener) {
        onPageChangeListeners.remove(l)
    }

    internal fun dispatchOnPageSelected(pos: Int) {
        for (l in onPageChangeListeners) {
            l.onPageSelected(pos)
        }
    }

    internal fun dispatchOnPageScrollStateChanged(state: Int) {
        for (l in onPageChangeListeners) {
            l.onPageScrollState(state)
        }
    }
}