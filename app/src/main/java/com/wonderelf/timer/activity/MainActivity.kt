package com.wonderelf.timer.activity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.widget.GridLayoutManager
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.remair.util.LogUtils
import com.wonderelf.timer.R
import com.wonderelf.timer.adapter.MainAdapter
import com.wonderelf.timer.base.BaseActivity
import com.wonderelf.timer.bean.MeatBean
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.title_bar.*
import org.jetbrains.anko.toast
import com.wonderelf.timer.adapter.SpacingItemDecoration
import com.wonderelf.timer.base.XApplication
import com.wonderelf.timer.util.NotificationUtil
import com.wonderelf.timer.util.SaveImageUtils


/**
 * Author: cl
 * Time: 2018/11/8
 * Description: 主页
 */
class MainActivity : BaseActivity() {

    private var list = ArrayList<MeatBean>()

    override fun initUI() {
        setContentView(R.layout.activity_main)
        tv_left_title.visibility = View.VISIBLE
        ll_main.visibility = View.VISIBLE
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
            toast("声音设置")
        }
        iv_clock.setOnClickListener {
            val intent = Intent(this@MainActivity, AllDetailActivity::class.java)
            startActivity(intent)
        }
//        SaveImageUtils().initPhoto(this@MainActivity)

        // 判断通知
        val isEnable = NotificationUtil.isNotificationEnabled(this)
        if (isEnable){
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
        val adapter = MainAdapter(this@MainActivity, list, spanCount, spaceWidth)
        recycler_view.layoutManager = GridLayoutManager(this@MainActivity, spanCount)
        recycler_view.addItemDecoration(SpacingItemDecoration(spaceWidth, spaceWidth))
        recycler_view.adapter = adapter
        adapter.setOnItemClick(object : MainAdapter.ItemClicker {
            override fun getData(bean: MeatBean, position: Int) {
                val intent = Intent(this@MainActivity, TypeActivity::class.java)
                intent.putExtra("tag", position)
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
}
