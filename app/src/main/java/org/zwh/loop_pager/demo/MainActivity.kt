package org.zwh.loop_pager.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.zwh.loop_pager.LoopLayoutManager
import org.zwh.loop_pager.curSelectedPage
import org.zwh.loop_pager.setLoop

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = PictureAdapter(this)
        rv.layoutManager = LoopLayoutManager(this)
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

}