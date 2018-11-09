package com.wonderelf.timer.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.wonderelf.timer.R
import com.wonderelf.timer.bean.MeatBean
import kotlinx.android.synthetic.main.item_main.view.*

class MainAdapter(context1: Context, list2: ArrayList<MeatBean>) : RecyclerView.Adapter<MainAdapter.ViewHodler>() {

    var context = context1
    var list = list2
    var click: ItemClicker? = null
//    var arrayPicNormal: IntArray = intArrayOf(R.drawable.icon_meat, R.drawable.icon_meat)

    override fun onBindViewHolder(holder: ViewHodler, position: Int) {
        with(holder.itemView) {
            iv_meat.setImageResource(list[position].img)
            tv_name.text = list[position].name
            holder.itemView.setOnClickListener {
                click?.getData(list[position])
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ViewHodler {
        return ViewHodler(View.inflate(p0.context, R.layout.item_main, null))
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHodler(item: View) : RecyclerView.ViewHolder(item)

    interface ItemClicker {
        fun getData(bean: MeatBean)
    }

    fun setOnItemClick(click: ItemClicker) {
        this.click = click
    }
}