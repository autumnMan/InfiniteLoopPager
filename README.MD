基于RecyclerView实现的可无限循环的"ViewPager"
================================================

原理比较简单：

其实就是通过继承LayoutManager来实现横向无限循环的View，并使用了PagerSnapHelper实现整页滑动和回弹效果

## 使用方法

在布局中直接使用RecyclerView
```xml
<androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

在代码中设置 **LoopLayoutManager**
```kotlin
val adapter = PictureAdapter(this)
rv.layoutManager = LoopLayoutManager(this) // 设置支持循环的LayoutManager
rv.addOnPageChangeListener(pageListener)
rv.adapter = adapter
rv.setLoop(true) // 设置是否可循环
```


## 主要实现LayoutManager的方法
```kotlin
fun onAttachedToWindow(view: RecyclerView)

fun scrollToPosition(position: Int)

fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int)

// 判断滑动的方向，向前or向后
fun computeScrollVectorForPosition(targetPosition: Int)

fun canScrollHorizontally()

// 滑动距离 dx，根据dx重新填充子View，并回收不可见的子View
fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State)
```

## 实现page滑动的关键类

RecyclerView 自带的 **PagerSnapHelper**

```kotlin
override fun onAttachedToWindow(view: RecyclerView) {
    super.onAttachedToWindow(view)
    if (recyclerView != view) {
        recyclerView = view
        snapHelper.attachToRecyclerView(view)
        // ..................
    }
}
    
internal val snapHelper: SnapHelper = object : PagerSnapHelper() {
    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
        val itemCount = layoutManager.itemCount
        val pos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
        if (pos >= itemCount) {
            return 0
        }
        return pos
    }
}
```