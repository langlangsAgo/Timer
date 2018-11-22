package com.wonderelf.timer.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.remair.util.ScreenUtils
import com.wonderelf.timer.R
import com.wonderelf.timer.base.XApplication
import com.wonderelf.timer.bean.MeatBean
import kotlinx.android.synthetic.main.item_main.view.*

class MainAdapter(context1: Context, list2: ArrayList<MeatBean>, spanCount: Int, spaceWidth: Int) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    var context = context1
    var list = list2
    var click: ItemClicker? = null
    var count = spanCount
    var space = spaceWidth

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView) {
            val screenWidth = ScreenUtils.getScreenWidth(XApplication.instance())
            var itemImgWidth = (screenWidth - space * (count + 1)) / count
            rl_bg.layoutParams = RelativeLayout.LayoutParams(itemImgWidth, itemImgWidth)

            iv_meat.setImageResource(list[position].img)
            tv_name.text = list[position].name
            rl_bg.setBackgroundResource(list[position].bg)
            holder.itemView.setOnClickListener {
                click?.getData(list[position], position)
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View.inflate(p0.context, R.layout.item_main, null))
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item)

    interface ItemClicker {
        fun getData(bean: MeatBean, position: Int)
    }

    fun setOnItemClick(click: ItemClicker) {
        this.click = click
    }
}