package com.wonderelf.timer.activity

import android.support.v7.widget.SimpleItemAnimator
import android.util.Log
import android.view.View
import com.remair.util.LogUtils
import com.wonderelf.timer.R
import com.wonderelf.timer.adapter.RunningDetailAdapter
import com.wonderelf.timer.adapter.GridLayoutManagerNoBug
import com.wonderelf.timer.adapter.SpacingItemDecoration
import com.wonderelf.timer.base.BaseActivity
import com.wonderelf.timer.bean.TypeDetailBean
import com.wonderelf.timer.countdowntime.CountDownTimerSupport
import com.wonderelf.timer.countdowntime.OnCountDownTimerListener
import com.wonderelf.timer.countdowntime.TimerState
import kotlinx.android.synthetic.main.activity_type.*
import kotlinx.android.synthetic.main.title_bar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast

/**
 * Author: cl
 * Time: 2018/11/19
 * Description: 展示所有正在倒计时的菜单
 */
class AllDetailActivity : BaseActivity() {

    private var adapterRunning: RunningDetailAdapter? = null
    private var position = 0 // 选中的position
    private var timerMap = mutableMapOf<Int, CountDownTimerSupport>()
    private var delList = mutableListOf<TypeDetailBean>() //缓存将要删除的数据

    companion object {
        var allList = mutableListOf<TypeDetailBean>() //数据源
    }

    override fun initUI() {
        setContentView(R.layout.activity_type)
        EventBus.getDefault().register(this)
        iv_close.visibility = View.VISIBLE
        tv_title.text = "正在计时"
        iv_close.setOnClickListener {
            finish()
        }
    }

    override fun initData() {
        if (allList.isNotEmpty()) {
            for (i in allList.indices) {
                if (allList[i].state == TimerState.FINISH) {
                    delList.add(allList[i]) //将要删除的数据添加进缓存
                }
            }
            // 删除已结束的倒计时
            if (delList.isNotEmpty()){
                for (i in delList.indices){
                    if (allList.contains(delList[i])){
                        allList.remove(delList[i])
                    }
                }
            }
            delList.clear() // 清空缓存
            initAdapter()
        }
    }

    private fun initAdapter() {
        val spanCount = 2  //列数
        val space = 32 // item间距
        adapterRunning = RunningDetailAdapter(this@AllDetailActivity, allList, spanCount, space)
        recycler_view.layoutManager = GridLayoutManagerNoBug(this@AllDetailActivity, spanCount)
        recycler_view.addItemDecoration(SpacingItemDecoration(space, space))
        (recycler_view.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false//关闭动画,防止选中时闪烁
        recycler_view.adapter = adapterRunning

        adapterRunning?.setOnItemClick(object : RunningDetailAdapter.OnItemClickListener {
            // 点击重置按钮
            override fun onResetClick(position: Int) {
                if (timerMap[position] != null) {
                    timerMap[position]?.reset()
                    timerMap[position]?.start()
                    allList[position].state = TimerState.START // 重新开始后设置为START状态
                    allList[position].remainingTime = allList[position].totalTime // 重新设置倒计时时间
                    toast("重新开始第" + position + "个")
                } else {
                    toast("当前菜单未开始计时")
                }
            }

            // 单击
            override fun onItemClick(p: Int) {
                position = p
                // 非编辑状态
//                    initCountDownTime(position)
            }
        })
    }

    /**
     * 点击开始倒计时
     */
    fun initCountDownTime(position: Int) {
        val maxDuration = allList[position].totalTime // 设置倒计时时间
        when (allList[position].state) {
            TimerState.START -> {
                if (timerMap[position] != null) {
                    timerMap[position]?.pause()
                }
                allList[position].state = TimerState.PAUSE // 暂停后设置为PAUSE状态
                toast("暂停了第" + position + "个")
            }
            TimerState.PAUSE -> {
                if (timerMap[position] != null) {
                    timerMap[position]?.resume()
                }
                allList[position].state = TimerState.START // 继续后设置为START状态
                toast("继续第" + position + "个")
            }
            TimerState.FINISH -> {
                if (timerMap[position] != null) {
                    timerMap[position]?.reset()
                    timerMap[position]?.start()
                }
                allList[position].state = TimerState.START // 重新开始后设置为START状态
                allList[position].remainingTime = allList[position].totalTime // 重新设置倒计时时间
                toast("重新开始第" + position + "个")
            }
            TimerState.DEFAULT -> {
                var mTimer: CountDownTimerSupport? = null
                allList[position].state = TimerState.START // 开始倒计时,设置为START状态
                if (mTimer == null) {
                    mTimer = CountDownTimerSupport(maxDuration, 1000)
                    mTimer.setOnCountDownTimerListener(object : OnCountDownTimerListener {
                        override fun onTick(millisUntilFinished: Long) {
                            Log.d("CountDownTimerSupport", "onTick : " + millisUntilFinished + "ms")
                            if (allList[position].state == TimerState.START) {
                                var newTime = millisUntilFinished
                                allList[position].remainingTime = Math.max(0, newTime)
                                adapterRunning?.startTime(position, allList[position])
                            }
                        }

                        override fun onFinish() {
                            allList[position].state = TimerState.FINISH
                            toast("第" + position + "个倒计时结束")
                        }
                    })
                    mTimer.start()
                    AllDetailActivity.allList.add(allList[position])
                    timerMap.put(position, mTimer) //添加timer到map集合
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        if (timerMap.isNotEmpty()) {
            for (i in 0 until timerMap.size) {
                timerMap[i]?.stop()
            }
        }
    }

    /**
     * 1.实时更新倒计时
     * 2.倒计时结束自动删除
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateCountDownTime(bean: TypeDetailBean) {
        if (allList.isNotEmpty()) {
            if (allList.contains(bean)) {
                for (i in allList.indices) {
                    when (bean.state) {
                        TimerState.START -> {
                            adapterRunning?.startTime(i, allList[i])
                        }
                        TimerState.FINISH -> {
                            adapterRunning?.removeList(bean)
                        }
                    }
                }
            }
        }
    }
}