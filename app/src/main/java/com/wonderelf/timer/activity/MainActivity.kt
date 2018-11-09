package com.wonderelf.timer.activity

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.wonderelf.timer.R
import com.wonderelf.timer.adapter.GridSpacingItemDecoration
import com.wonderelf.timer.adapter.MainAdapter
import com.wonderelf.timer.base.BaseActivity
import com.wonderelf.timer.bean.MeatBean
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Author: cl
 * Time: 2018/11/8
 * Description:
 */
class MainActivity : BaseActivity() {

    var adapter: MainAdapter? = null
    var list = ArrayList<MeatBean>()

    override fun initUI() {
        setContentView(R.layout.activity_main)
    }

    override fun initData() {
        var bean = MeatBean()
        bean.img = R.drawable.icon_meat
        bean.name = "肉类"

        var bean2 = MeatBean()
        bean2.img = R.drawable.icon_veg
        bean2.name = "蔬菜类"

        list.add(bean)
        list.add(bean2)
        list.add(bean)
        list.add(bean2)
        list.add(bean2)
        adapter = MainAdapter(this@MainActivity, list)
        recycler_view.layoutManager = GridLayoutManager(this@MainActivity, 2)
        recycler_view.addItemDecoration(GridSpacingItemDecoration(2, 32, false))
        recycler_view.adapter = adapter
    }
}
