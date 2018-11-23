package com.wonderelf.timer.activity

import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.KeyEvent
import android.view.View
import com.remair.util.ACache
import com.remair.util.LogUtils
import com.wonderelf.timer.R
import com.wonderelf.timer.adapter.MainAdapter
import com.wonderelf.timer.adapter.SpacingItemDecoration
import com.wonderelf.timer.base.BaseActivity
import com.wonderelf.timer.bean.MeatBean
import com.wonderelf.timer.bean.SharedKey
import com.wonderelf.timer.bean.TypeDetailBean
import com.wonderelf.timer.util.NotificationUtil
import com.wonderelf.timer.util.SharedPreferenceUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.title_bar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


/**
 * Author: cl
 * Time: 2018/11/8
 * Description: 主页
 */
class MainActivity : BaseActivity() {

    private var list = ArrayList<MeatBean>()
    private var adapter: MainAdapter? = null
    private var position = 0

    companion object {
        var allList = mutableListOf<TypeDetailBean>() //数据源
    }

    override fun initUI() {
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        tv_left_title.visibility = View.VISIBLE
        ll_main.visibility = View.VISIBLE
        val isQuiet = SharedPreferenceUtil.getBoolean(SharedKey.isQuiet, false)
        if (isQuiet) {
            icon_voice.setImageResource(R.drawable.icon_quiet)
        } else {
            icon_voice.setImageResource(R.drawable.icon_voice)
        }
    }

    override fun initData() {

        var bean = MeatBean()
        bean.img = R.drawable.icon_meat
        bean.name = resources.getText(R.string.tv_meat).toString()
        bean.bg = R.drawable.shape_bg_meat

        var bean2 = MeatBean()
        bean2.img = R.drawable.icon_veg
        bean2.name = resources.getText(R.string.tv_veg).toString()
        bean2.bg = R.drawable.shape_bg_veg

        list.add(bean)
        list.add(bean2)

        initAdapter()
        iv_voice.setOnClickListener {
            val isQuiet = SharedPreferenceUtil.getBoolean(SharedKey.isQuiet, false)
            if (isQuiet) {
                SharedPreferenceUtil.putBoolean(SharedKey.isQuiet, false)
                icon_voice.setImageResource(R.drawable.icon_voice)
            } else {
                SharedPreferenceUtil.putBoolean(SharedKey.isQuiet, true)
                icon_voice.setImageResource(R.drawable.icon_quiet)
            }
        }
        iv_clock.setOnClickListener {
            val intent = Intent(this@MainActivity, AllDetailActivity::class.java)
            startActivity(intent)
        }
//        SaveImageUtils().initPhoto(this@MainActivity)

        // 判断通知
        val isEnable = NotificationUtil.isNotificationEnabled(this)
        if (isEnable) {
            // 判断channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationUtil(this).manager.getNotificationChannel(NotificationUtil.id)
                if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                    //                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                    //                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    //                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                    //                startActivity(intent);
                }
            }
        }
    }

    /**
     * 加载列表数据
     */
    private fun initAdapter() {
        val spanCount = 2 // 列数
        val spaceWidth = 32 // item间距 px
        adapter = MainAdapter(this@MainActivity, list, spanCount, spaceWidth)
        recycler_view.layoutManager = GridLayoutManager(this@MainActivity, spanCount)
        recycler_view.addItemDecoration(SpacingItemDecoration(spaceWidth, spaceWidth))
        (recycler_view.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false//关闭动画,防止选中时闪烁
        recycler_view.adapter = adapter
        adapter?.setOnItemClick(object : MainAdapter.ItemClicker {
            override fun getData(bean: MeatBean, p: Int) {
                for (i in TypeActivity.timerMap.iterator()){

                }
                val intent = Intent(this@MainActivity, TypeActivity::class.java)
                intent.putExtra("tag", p)
                position = p
                startActivity(intent)
            }
        })
    }

    /**
     * 首页点击返回,应用保持后台运行
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (!(keyCode != KeyEvent.KEYCODE_BACK || event?.action != KeyEvent.ACTION_DOWN)) {
            moveTaskToBack(true)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    /**
     * 实时更新倒计时信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun initTimeState(bean: TypeDetailBean) {
        // 通过缓存数据进行判断
//        try {
//            val cacheList = ACache.get(this).getAsObject(SharedKey.tag_list + position) as MutableList<TypeDetailBean>
//
//        } catch (e: Exception) {
//        }

        // 获取所有倒计时中剩余时长最多的一项进行转圈显示
        var timeList = mutableListOf<Long>()
        if (AllDetailActivity.allList.contains(bean)) { // 如果正在倒计时集合数据中包含接收的数据
            for (i in AllDetailActivity.allList.indices) {
                timeList.add(AllDetailActivity.allList[i].remainingTime) // 所有剩余时间添加到集合
            }
        }
        var time = Collections.max(timeList) // 获取集合中最大一个剩余时间
        for (i in AllDetailActivity.allList.indices) {
            if (AllDetailActivity.allList[i].remainingTime == time) {
                // 剩余倒计时最长的一项进行动画展示
                adapter?.startTime(position, AllDetailActivity.allList[i])
            }
        }
    }
}
