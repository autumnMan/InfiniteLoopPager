package org.zwh.loop_pager.demo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.zwh.loop_pager.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = PictureAdapter(this)
        rv.layoutManager = LoopLayoutManager(this)
        rv.addOnPageChangeListener(pageListener)
        rv.adapter = adapter
        rv.setLoop(true)

        pre.setOnClickListener {
            val curPos = rv.curSelectedPage()
            rv.scrollToPosition(if (curPos <= 0) adapter.itemCount - 1 else curPos - 1)
        }
        next.setOnClickListener {
            val curPos = rv.curSelectedPage()
            rv.scrollToPosition(if (curPos <= 0) 1 else curPos + 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rv.removeOnPageChangeListener(pageListener)
    }

    private var toast: Toast? = null

    private val pageListener = object : OnPageChangeListener {
        override fun onPageSelected(pos: Int) {
            Log.d(TAG, "selected page: $pos")
            toast?.cancel()
            toast = Toast.makeText(this@MainActivity, "selected --> $pos", Toast.LENGTH_LONG)
            toast?.show()
        }

        override fun onPageScrollState(state: Int) {
//            Log.d(TAG, "page scroll state: $state")
        }
    }
}