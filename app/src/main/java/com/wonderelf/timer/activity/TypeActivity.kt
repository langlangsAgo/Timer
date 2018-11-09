package com.wonderelf.timer.activity

import com.wonderelf.timer.R
import com.wonderelf.timer.base.BaseActivity
import kotlinx.android.synthetic.main.activity_start.*

/**
 * Author: cl
 * Time: 2018/11/9
 * Description:
 */
class TypeActivity : BaseActivity() {

    override fun initUI() {
        setContentView(R.layout.activity_start)
    }

    override fun initData() {
        iv_reset.setOnClickListener {
            val n = (Math.random() * 100).toInt()
            maskImageView.setPercent(n)
        }

        maskImageView.setOnClickListener {
            maskImageView.isIsShowMaskOnClick = !maskImageView.isIsShowMaskOnClick
        }
    }

}