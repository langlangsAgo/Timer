package com.wonderelf.timer.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.remair.util.ScreenUtils
import com.remair.util.TimeUtils
import com.wonderelf.timer.R
import com.wonderelf.timer.base.XApplication
import com.wonderelf.timer.bean.TypeDetailBean
import com.wonderelf.timer.countdowntime.TimerState
import kotlinx.android.synthetic.main.item_type_detail.view.*

class DetailAdapter(context1: Context, list2: MutableList<TypeDetailBean>, spanCount: Int, spaceWidth: Int) : RecyclerView.Adapter<DetailAdapter.ViewHolder>(), View.OnClickListener, View.OnLongClickListener {

    private var context = context1
    private var list = list2 //数据源
    private var click: OnItemClickListener? = null //单点
    private var longClick: OnLongItemClickListener? = null //长按
    private var isEdit = false // 是否显示编辑框
    private val count = spanCount // 列数
    private val space = spaceWidth // item间隔
    private var isSelect = 0 // 是否选中复选框 0:未选中  1:选中
    private var isLong = false // 已经长按

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView) {
            val screenWidth = ScreenUtils.getScreenWidth(XApplication.instance())
            //计算单列的宽度
            var itemImgWidth = (screenWidth - space * (count + 1)) / count
            //设置单列宽度
            maskImageView.layoutParams = RelativeLayout.LayoutParams(itemImgWidth, itemImgWidth)
            if (list[position].isDefault == 1) { // 1为默认数据  直接添加drawable图片
                Glide.with(context).load(list[position].img.toInt())
                        .apply(RequestOptions().placeholder(R.drawable.img_clock)).into(maskImageView)
            } else {
                Glide.with(context).load(list[position].img)
                        .apply(RequestOptions().placeholder(R.drawable.img_clock)).into(maskImageView)
            }
            if (isEdit) {
                iv_check.visibility = View.VISIBLE
            } else {
                iv_check.visibility = View.GONE
            }
            if (list[position].isSelect == 1) {
                iv_check.setImageResource(R.drawable.icon_checkmark)
            } else {
                iv_check.setImageResource(R.drawable.icon_checkmark_circle)
            }
            tv_name.text = list[position].name
            val totalTime = TimeUtils.mills2Time(list[position].totalTime)
            val remainingTime = TimeUtils.mills2Time(list[position].remainingTime)
            iv_reset.visibility = View.VISIBLE
            when (list[position].state) {
                TimerState.START -> {
                    tv_time.text = remainingTime
                    tv_time.setTextColor(Color.parseColor("#404040"))
                    tv_name.setTextColor(Color.parseColor("#404040"))
                }
                TimerState.PAUSE -> {
                    tv_time.text = remainingTime
                    tv_time.setTextColor(Color.parseColor("#404040"))
                    tv_name.setTextColor(Color.parseColor("#404040"))
                }
                TimerState.FINISH -> {
                    tv_time.text = "00:00:00"
                    tv_time.setTextColor(Color.parseColor("#F65454"))
                    tv_name.setTextColor(Color.parseColor("#F65454"))
                }
                TimerState.DEFAULT -> {
                    iv_reset.visibility = View.GONE
                    tv_time.text = totalTime
                    tv_time.setTextColor(Color.parseColor("#404040"))
                    tv_name.setTextColor(Color.parseColor("#404040"))
                }
            }

            iv_reset.setOnClickListener {
                if (click != null) {
                    click?.onResetClick(position)
                }
            }
            holder.itemView.setOnClickListener(this@DetailAdapter)
            holder.itemView.setOnLongClickListener(this@DetailAdapter)
            holder.itemView.tag = position
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View.inflate(context, R.layout.item_timer_view, null))
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item)

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onResetClick(position: Int)
    }

    interface OnLongItemClickListener {
        fun onLongClick(position: Int)
    }

    fun setOnLongItemClick(longClick: OnLongItemClickListener) {
        this.longClick = longClick
    }

    fun setOnItemClick(click: OnItemClickListener) {
        this.click = click
    }


    /**
     * 点击编辑按钮出现复选框
     */
    fun showChecked(isEdits: Boolean) {
        isEdit = isEdits
        notifyDataSetChanged()
    }

    /**
     * 选中某个
     */
    fun selectItem(position: Int, isSelects: Int) {
        isSelect = isSelects
        list[position].isSelect = isSelect
        notifyItemChanged(position)
    }

    /**
     * 还原选中状态
     */
    fun clearSelectState(position: Int) {
        list[position].isSelect = 0
        notifyItemChanged(position)
    }

    /**
     * 删除多个
     */
    fun removeList(bean: TypeDetailBean) {
        for (i in list.indices) {
            if (list.contains(bean)) {
                var position = list.indexOf(bean)
                list.remove(bean)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
            }
        }
    }

    /**
     * 添加数据集合
     */
    fun addItem(bean: TypeDetailBean) {
        list.add(bean)
        notifyItemInserted(list.size - 1)
        notifyItemRangeChanged(list.size - 1, list.size - (list.size - 1))
    }

    /**
     * 修改第position条数据
     */
    fun changeItem(position: Int, bean: TypeDetailBean) {
        list[position].totalTime = bean.totalTime
        list[position].name = bean.name
        list[position].img = bean.img
        list[position].isDefault = bean.isDefault
        notifyItemChanged(position)
    }

    /**
     * 开始倒计时
     */
    fun startTime(position: Int, bean: TypeDetailBean) {
        list[position].totalTime = bean.totalTime
        list[position].remainingTime = bean.remainingTime
        list[position].state = bean.state
        notifyItemChanged(position)
    }

    override fun onClick(v: View?) {
        if (click != null) {
            click?.onItemClick(v?.tag as Int)
        }
    }

    override fun onLongClick(v: View?): Boolean {
        if (longClick != null) {
            longClick?.onLongClick(v?.tag as Int)
        }
        return true
    }
}