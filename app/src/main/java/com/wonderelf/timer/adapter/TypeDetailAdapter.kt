package com.wonderelf.timer.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.remair.util.LogUtils
import com.remair.util.ScreenUtils
import com.remair.util.TimeUtils
import com.wonderelf.timer.R
import com.wonderelf.timer.base.XApplication
import com.wonderelf.timer.bean.TypeDetailBean
import com.wonderelf.timer.countdowntime.TimerState
import com.wonderelf.timer.view.TimerView
import kotlinx.android.synthetic.main.item_type_detail.view.*

class TypeDetailAdapter(context1: Context, list2: MutableList<TypeDetailBean>,
                        spanCount: Int, spaceWidth: Int) : RecyclerView.Adapter<TypeDetailAdapter.ViewHolder>(),
        View.OnClickListener, View.OnLongClickListener, TimerView.OnCountdownFinishListener {

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

            if (list[position].isSelect == 1) {
                iv_check.setImageResource(R.drawable.icon_checkmark)
            } else {
                iv_check.setImageResource(R.drawable.icon_checkmark_circle)
            }
            tv_name.text = list[position].name
            val totalTime = TimeUtils.mills2Time(list[position].totalTime)
            val remainingTime = TimeUtils.mills2Time(list[position].remainingTime)
            // 倒计时状态
            when (list[position].state) {
                TimerState.START -> {
                    iv_reset.visibility = View.VISIBLE
                    tv_time.text = remainingTime
                    tv_time.setTextColor(Color.parseColor("#404040"))
                    tv_name.setTextColor(Color.parseColor("#404040"))
                    val progress = ((list[position].totalTime - list[position].remainingTime) / list[position].totalTime.toDouble() * 100).toFloat()
                    maskImageView.setCountdownTime(list[position].totalTime, progress, true, position)
                }
                TimerState.PAUSE -> {
                    tv_time.text = remainingTime
                    tv_time.setTextColor(Color.parseColor("#404040"))
                    tv_name.setTextColor(Color.parseColor("#404040"))
                    maskImageView.setAnimatorPause()
                }
                TimerState.RESUME -> {
                    tv_time.text = remainingTime
                    tv_time.setTextColor(Color.parseColor("#404040"))
                    tv_name.setTextColor(Color.parseColor("#404040"))
                    maskImageView.setAnimatorResume(list[position].remainingTime)
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
            if (isEdit) {
                iv_check.visibility = View.VISIBLE
            } else {
                iv_check.visibility = View.GONE
            }

            iv_reset.setOnClickListener {
                if (click != null) {
                    click?.onResetClick(position)
                }
            }
            holder.itemView.setOnClickListener(this@TypeDetailAdapter)
            holder.itemView.setOnLongClickListener(this@TypeDetailAdapter)
            holder.itemView.tag = position
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View.inflate(context, R.layout.item_type_detail, null))
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

    // 单击
    override fun onClick(v: View?) {
        if (click != null) {
            click?.onItemClick(v?.tag as Int)
        }
    }

    // 长按
    override fun onLongClick(v: View?): Boolean {
        if (longClick != null) {
            longClick?.onLongClick(v?.tag as Int)
        }
        return true
    }

    /**
     * 暂停动画
     */
    fun setAnimatorState(position: Int, bean: TypeDetailBean) {
        list[position].totalTime = bean.totalTime
        list[position].remainingTime = bean.remainingTime
        list[position].state = bean.state
        notifyItemChanged(position)
    }

    override fun onAnimationStart(position: Int, currentProgress: Float) {
        LogUtils.e("----------onAnimationStart=$position+process=$currentProgress")
    }

    override fun onAnimatorPause(position: Int, currentProgress: Float) {
        LogUtils.e("----------onAnimatorPause=$position+process=$currentProgress")
    }

    override fun onAnimatorResume(position: Int, currentProgress: Float) {
        LogUtils.e("----------onAnimatorResume=$position+process=$currentProgress")
    }

    override fun onAnimatorCancel(position: Int, currentProgress: Float) {
        LogUtils.e("----------onAnimatorCancel=$position+process=$currentProgress")
    }

    override fun onAnimatorEnd(position: Int, currentProgress: Float) {
        LogUtils.e("----------onAnimatorEnd=$position+process=$currentProgress")
    }

    override fun onAnimatorRunning(position: Int, currentProgress: Float) {
        val time = ((100 - currentProgress) / 100 * list[position].totalTime).toLong()
        LogUtils.e("----------还有时间=" + Math.ceil((time / 1000).toDouble()))
//        LogUtils.e("----------还有时间=" + TimeUtils.mills2Time2((Math.ceil((time / 1000).toDouble()) * 1000).toLong()))
        list[position].remainingTime = time

    }
}