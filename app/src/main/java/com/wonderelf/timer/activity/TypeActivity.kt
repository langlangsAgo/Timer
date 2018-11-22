package com.wonderelf.timer.activity

import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.remair.util.ACache
import com.remair.util.LogUtils
import com.wonderelf.timer.R
import com.wonderelf.timer.adapter.SpacingItemDecoration
import com.wonderelf.timer.adapter.TypeDetailAdapter
import com.wonderelf.timer.base.BaseActivity
import com.wonderelf.timer.bean.SharedKey
import com.wonderelf.timer.bean.TypeDetailBean
import com.wonderelf.timer.countdowntime.CountDownTimerSupport
import com.wonderelf.timer.countdowntime.OnCountDownTimerListener
import com.wonderelf.timer.countdowntime.PaintState
import com.wonderelf.timer.countdowntime.TimerState
import com.wonderelf.timer.util.NotificationUtil
import com.wonderelf.timer.util.SharedPreferenceUtil
import com.wonderelf.timer.view.PopupDelType
import kotlinx.android.synthetic.main.activity_type.*
import kotlinx.android.synthetic.main.item_type_detail.*
import kotlinx.android.synthetic.main.item_type_detail.view.*
import kotlinx.android.synthetic.main.title_bar.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast
import java.io.Serializable


/**
 * Author: cl
 * Time: 2018/11/9
 * Description: 某个单一类型
 */
class TypeActivity : BaseActivity(), PopupDelType.onItemOnClickListener {

    private var isEdit = false //是否进入编辑状态
    private var position = 0 // 选中的position
    private var tag = 0 // 类别
    private var adapter: TypeDetailAdapter? = null
    private var delList = mutableListOf<TypeDetailBean>() // 将要删除的菜单集合
    private var list = mutableListOf<TypeDetailBean>() //数据源

    private var timerMap = mutableMapOf<Int, CountDownTimerSupport>() //添加所有正在计时的timer

    companion object {
        const val typeRequest = 1001
    }

    override fun initUI() {
        setContentView(R.layout.activity_type)
        iv_back.visibility = View.VISIBLE
        ll_type.visibility = View.VISIBLE
    }

    override fun initData() {
        tag = intent.getIntExtra("tag", 0)
        iv_back.setOnClickListener {
            if (isEdit) {
                initState()
                clearState()
            } else {
                finish()
            }
        }
        iv_add.setOnClickListener {
            startActivityForResult(Intent(this@TypeActivity, AddTypeActivity::class.java), typeRequest)
        }
        // 进入编辑状态
        iv_edit.setOnClickListener {
            if (list.isEmpty() && list.size == 0) {
                toast("请添加数据")
            } else {
                ll_type.visibility = View.GONE
                ll_edit.visibility = View.VISIBLE
                isEdit = true
                adapter?.showChecked(isEdit)
            }
        }
        iv_clock2.setOnClickListener {
            val intent = Intent(this@TypeActivity, AllDetailActivity::class.java)
            startActivity(intent)
        }
        // 编辑
        tv_edit.setOnClickListener {
            if (delList.isEmpty() && delList.size == 0) {
                toast("请选择菜单")
            } else if (delList.size > 1) {
                toast("只能编辑单个菜单")
            } else {
                val intent = Intent(this@TypeActivity, AddTypeActivity::class.java)
                intent.putExtra("isEdit", true)
                intent.putExtra("item_bean", delList[0])
                if (list.contains(delList[0])) {
                    position = list.indexOf(delList[0]) //选择多个后取剩下的最后一个在list中的position
                }
                startActivityForResult(intent, typeRequest)
            }
        }
        // 删除
        tv_clear.setOnClickListener {
            if (delList.isEmpty() && delList.size == 0) {
                toast("请选择菜单")
            } else {
                val pop = PopupDelType(this)
                pop.listener = this@TypeActivity
                pop.showPopupWindow()
            }
        }
        getSdCardData()
    }

    /**
     * 物理返回键
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (!(keyCode != KeyEvent.KEYCODE_BACK || event?.action != KeyEvent.ACTION_DOWN)) {
            if (isEdit) {
                initState()
                clearState()
            } else {
                onBackPressed()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 读取数据
     */
    private fun getSdCardData() {
        // 是否第一次进入加载本地数据
        val isFirstIn = SharedPreferenceUtil.getBoolean(SharedKey.Companion.isFirstIn, true)
        if (isFirstIn) {
            initDefaultData()
        } else {
            try {
                list = ACache.get(this).getAsObject(SharedKey.tag_list + tag) as MutableList<TypeDetailBean>
            } catch (e: Exception) {
                initDefaultData()
            }
        }
        initAdapter()
    }

    /**
     * 第一次进入添加默认数据
     */
    private fun initDefaultData() {
        // 肉类默认数据
        val dataMeat = listOf("毛肚", "鸭肠", "肥牛卷", "鹌鹑蛋", "鱿鱼", "虾")
        val dataMeatTime = listOf(10000L, 10000L, 60 * 1000L, 60 * 1000L, 120 * 1000L, 300 * 1000L)
        val dataMeatImg = listOf(R.drawable.img_omasum, R.drawable.img_intestine, R.drawable.img_cattle, R.drawable.img_egg, R.drawable.img_squid, R.drawable.img_shrimp)
        // 蔬菜类
        val dataVeg = listOf("香菜", "莲藕", "豆皮", "海带", "菌菇类")
        val dataVegTime = listOf(60 * 1000L, 120 * 1000L, 120 * 1000L, 300 * 1000L, 300 * 1000L)
        val dataVegImg = listOf(R.drawable.img_coriander, R.drawable.img_lotusroot, R.drawable.img_bean, R.drawable.img_cabbage, R.drawable.img_mushroom)
        when (tag) {
            0 -> {
                for (i in dataMeat.indices) {
                    val bean = TypeDetailBean()
                    bean.name = dataMeat[i]
                    bean.totalTime = dataMeatTime[i]
                    bean.remainingTime = dataMeatTime[i]
                    bean.img = dataMeatImg[i].toString()
                    bean.positionId = i
                    list.add(bean)
                }
            }
            1 -> {
                for (i in dataVeg.indices) {
                    val bean = TypeDetailBean()
                    bean.name = dataVeg[i]
                    bean.totalTime = dataVegTime[i]
                    bean.remainingTime = dataVegTime[i]
                    bean.img = dataVegImg[i].toString()
                    bean.positionId = i
                    list.add(bean)
                }
            }
        }
    }

    /**
     * 加载列表数据
     */
    private fun initAdapter() {
        val spanCount = 2  //列数
        val space = 32 // item间距
        adapter = TypeDetailAdapter(this@TypeActivity, list, spanCount, space)
        recycler_view.layoutManager = GridLayoutManager(this@TypeActivity, spanCount)
        recycler_view.addItemDecoration(SpacingItemDecoration(space, space))
        (recycler_view.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false//关闭动画,防止选中时闪烁
        recycler_view.adapter = adapter
//        recycler_view.addOnItemTouchListener(RecyclerViewClickListener(this, recycler_view,
//                object : RecyclerViewClickListener.OnItemClickListener {
//                    override fun onItemClick(view: View?, p: Int) {
//                        position = p
//                        if (isEdit) {
//                            // 编辑状态
////                            adapter?.selectItem(position, if (list[position].isSelect == 0) 1 else 0)
//                            if (list[position].isSelect == 0) {
//                                adapter?.selectItem(position, 1)
//                                // 添加将要删除的数据集合
//                                delList.add(list[position])
//                            } else {
//                                adapter?.selectItem(position, 0)
//                                // 勾选成功后再次点击去除勾选框时删除缓存delList中对应的数据
//                                for (i in delList.indices) {
//                                    if (delList.contains(list[position])) {
//                                        delList.remove(list[position])
//                                    }
//                                }
//                            }
//                        } else {
//                            // 非编辑状态
//                            initCountDownTime(position)
//                        }
//                    }
//
//                    override fun onItemLongClick(view: View?, position: Int) {
//                        ll_type.visibility = View.GONE
//                        ll_edit.visibility = View.VISIBLE
//                        isEdit = true
//                        adapter?.showChecked(isEdit)
//
//                    }
//                }))
        adapter?.setOnItemClick(object : TypeDetailAdapter.OnItemClickListener {
            // 点击重置按钮
            override fun onResetClick(position: Int) {
                if (timerMap[position] != null) {
                    timerMap[position]?.reset()
                    timerMap[position]?.start()
                    list[position].state = TimerState.START // 重新开始后设置为START状态
                    list[position].remainingTime = list[position].totalTime // 重新设置倒计时时间
                    toast("重新开始第" + position + "个")
                } else {
                    toast("当前菜单未开始计时")
                }
            }

            // 单击
            override fun onItemClick(p: Int) {
                position = p
                if (isEdit) {
                    // 编辑状态
//                            adapter?.selectItem(position, if (list[position].isSelect == 0) 1 else 0)
                    if (list[position].isSelect == 0) {
                        adapter?.selectItem(position, 1)
                        // 添加将要删除的数据集合
                        delList.add(list[position])
                    } else {
                        adapter?.selectItem(position, 0)
                        // 勾选成功后再次点击去除勾选框时删除缓存delList中对应的数据
                        for (i in delList.indices) {
                            if (delList.contains(list[position])) {
                                delList.remove(list[position])
                            }
                        }
                    }
                } else {
                    // 非编辑状态
                    initCountDownTime(position)
                }
            }
        })

        // 长按
        adapter?.setOnLongItemClick(object : TypeDetailAdapter.OnLongItemClickListener {
            override fun onLongClick(p: Int) {
                ll_type.visibility = View.GONE
                ll_edit.visibility = View.VISIBLE
                isEdit = true
                adapter?.showChecked(isEdit)
            }
        })
    }

    /**
     * 还原选择状态
     */
    private fun initState() {
        if (isEdit) {
            ll_type.visibility = View.VISIBLE
            ll_edit.visibility = View.GONE
            isEdit = false
            adapter?.showChecked(isEdit)
        }
    }

    /**
     * 清楚选中状态
     */
    private fun clearState() {
        for (i in list.indices) {
            if (list[i].isSelect == 1) {
                list[i].isSelect = 0
                adapter?.clearSelectState(i)
            }
        }
        delList.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == typeRequest) {
            // 进入编辑页面后还原选中状态,并恢复原页面(在此处方法中调用无闪现画面)
            initState()
            clearState()
            val time = data?.getLongExtra("item_time", 0)
            val name = data?.getStringExtra("item_name")
            val imgPath = data?.getStringExtra("item_img")
            val isDefault = data?.getIntExtra("item_is_default", 1)
            if (data != null) {
                // 修改或新增数据时设置原状态
                val b = TypeDetailBean()
                b.name = name!!
                b.img = imgPath!!
                b.isSelect = 0
                b.isDefault = isDefault!!
                b.totalTime = time!!
                b.remainingTime = time
                b.state = TimerState.DEFAULT
                if (resultCode == 1) {
                    // 修改
                    adapter?.changeItem(position, b)
                } else if (resultCode == 2) {
                    // 新增
                    b.positionId = list.size
                    adapter?.addItem(b)
                    // 增加之后移动到最后一个
                    recycler_view.smoothScrollToPosition(list.size - 1)
                }
                saveData()
            }
        }
    }

    /**
     * 缓存修改后的数据
     */
    private fun saveData() {
        SharedPreferenceUtil.putBoolean(SharedKey.isFirstIn, false)
        // 缓存此tag类别的数据
        ACache.get(this).put(SharedKey.Companion.tag_list + tag, list as Serializable)
    }

    /**
     * 点击开始倒计时
     */
    fun initCountDownTime(position: Int) {
        val maxDuration = list[position].totalTime // 设置倒计时时间
        when (list[position].state) {
            TimerState.START -> {
                // 暂停倒计时
                if (timerMap[position] != null) {
                    timerMap[position]?.pause()
                }
                list[position].state = TimerState.PAUSE // 暂停后设置为PAUSE状态
                adapter?.setAnimatorState(position, list[position])
                toast("暂停了第" + position + "个")
            }
            TimerState.PAUSE -> {
                // 继续倒计时
                if (timerMap[position] != null) {
                    timerMap[position]?.resume()
                }
                list[position].state = TimerState.RESUME // 继续后设置为RESUME状态
                adapter?.setAnimatorState(position, list[position])
                toast("继续第" + position + "个")
            }
            TimerState.RESUME ->{
                // 暂停倒计时
                if (timerMap[position] != null) {
                    timerMap[position]?.pause()
                }
                list[position].state = TimerState.PAUSE // 暂停后设置为PAUSE状态
                adapter?.setAnimatorState(position, list[position])
                toast("继续暂停第" + position + "个")
            }
            TimerState.FINISH -> {
                // 倒计时重置
                if (timerMap[position] != null) {
                    timerMap[position]?.reset()
                    timerMap[position]?.start()
                }
                list[position].state = TimerState.START // 重新开始后设置为START状态
                list[position].remainingTime = list[position].totalTime // 重新设置倒计时时间
                AllDetailActivity.allList.add(list[position]) // 添加到正在展示详情页
                toast("重新开始第" + position + "个")
            }
            TimerState.DEFAULT -> {
                // 开始倒计时,设置为START状态
                list[position].state = TimerState.START
                var mTimer: CountDownTimerSupport? = null
                if (mTimer == null) {
                    mTimer = CountDownTimerSupport(maxDuration, 1000)
                    mTimer.setOnCountDownTimerListener(object : OnCountDownTimerListener {
                        override fun onTick(millisUntilFinished: Long) {
                            Log.d("---CountDownTimer", "onTick : " + millisUntilFinished + "ms")
                            if (list[position].state == TimerState.START || list[position].state == TimerState.RESUME) {
                                var newTime = millisUntilFinished
                                list[position].remainingTime = Math.max(0, newTime)
                                // 列表开始倒计时
                                adapter?.startTime(position, list[position])
                                EventBus.getDefault().post(list[position])
                            }
                        }

                        override fun onFinish() {
                            list[position].state = TimerState.FINISH // 设置结束状态

                            // 计时结束删除已添加到所有正在展示的计时详情页数据
                            if (AllDetailActivity.allList.contains(list[position])) {
                                EventBus.getDefault().post(list[position])
                            }

                            // 启动服务,全局显示弹框
                            val intent = Intent("com.wonderelf.endtime")
                            intent.`package` = packageName
                            intent.putExtra("content", list[position].name)
                            startService(intent)
                            // 发送通知
                            NotificationUtil(this@TypeActivity)
                                    .sendNotification(resources.getText(R.string.app_name).toString(),
                                            list[position].name, (tag.toString() + position.toString()).toInt())
                        }
                    })
                    mTimer.start()
                    // 添加到正在展示详情页
                    AllDetailActivity.allList.add(0, list[position])
                    //添加timer到map集合,用于判断单个item计时状态
                    timerMap.put(position, mTimer)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        for (i in 0 until timerMap.size) {
            timerMap[i]?.stop()
        }
    }

    /**
     * 删除确认框里的取消按钮事件
     *
     */
    override fun onBtnNoClick() {
        // 不处理任何事情
    }

    /**
     * 删除确认框里的确定按钮事件
     */
    override fun onBtnYesClick() {
        for (i in delList.indices) {
            adapter?.removeList(delList[i])
            // 删除后,详情页中的数据也删除
            if (AllDetailActivity.allList.contains(delList[i])) {
                AllDetailActivity.Companion.removeListFromEdit(delList[i])
            }
        }
        // 删除后清空缓存list
        delList.clear()
        // 保存
        saveData()
    }

    override fun onResume() {
        super.onResume()
//        if (AllDetailActivity.Companion.allList.isNotEmpty()){
//            for (i in AllDetailActivity.Companion.allList.indices){
//                if (list.contains(AllDetailActivity.Companion.allList[i])){
//                    var position = list.indexOf(AllDetailActivity.Companion.allList[i])
//                    adapter?.startTime(position, AllDetailActivity.Companion.allList[i])
//                }
//            }
//        }
    }
}