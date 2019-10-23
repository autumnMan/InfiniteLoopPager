package org.zwh.loop_pager.demo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_img.view.*

class PictureAdapter(
        private val cxt: Context
) : RecyclerView.Adapter<MyHolder>() {
    private val TAG = "MyAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        Log.d(TAG, "create view")
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_img, parent, false)
        return MyHolder(v)
    }

    override fun getItemCount(): Int {
        return Constants.IMG_ARRAY.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.itemView.img.setImageResource(Constants.IMG_ARRAY[position])
    }
}

class MyHolder(
        item: View
) : RecyclerView.ViewHolder(item)