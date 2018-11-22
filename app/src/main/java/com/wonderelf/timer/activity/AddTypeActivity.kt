package com.wonderelf.timer.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.CustomListener
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.bumptech.glide.Glide
import com.remair.util.LogUtils
import com.remair.util.TimeUtils
import com.wonderelf.timer.R
import com.wonderelf.timer.base.BaseActivity
import com.wonderelf.timer.bean.TypeDetailBean
import com.wonderelf.timer.view.PopupSelectPic
import kotlinx.android.synthetic.main.activity_add_type.*
import kotlinx.android.synthetic.main.title_bar.*
import org.devio.takephoto.app.TakePhoto
import org.devio.takephoto.app.TakePhotoImpl
import org.devio.takephoto.model.*
import org.devio.takephoto.permission.InvokeListener
import org.devio.takephoto.permission.PermissionManager
import org.devio.takephoto.permission.TakePhotoInvocationHandler
import org.jetbrains.anko.toast
import java.io.File
import java.io.Serializable
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList


/**
 * Author: cl
 * Time: 2018/11/12
 * Description: 添加预设时间
 */
class AddTypeActivity : BaseActivity(), PopupSelectPic.onSelectOnClickListener, TakePhoto.TakeResultListener, InvokeListener {

    private val hour = mutableListOf<String>()
    private val minute = mutableListOf<String>()
    private val second = mutableListOf<String>()
    private var takePhoto: TakePhoto? = null
    private var invokeParam: InvokeParam? = null
    private val TAG = AddTypeActivity::class.java.name

    private var isEdit = false  //是否从"编辑"按钮进入
    private var selectTime = "" // 选择的时间
    private var selectImg = "" // 选择的图片
    private var beanType: TypeDetailBean? = null//数据源
    private var isImgNotChanged = 1  // 是否更改图片 1:没有更改 0:更改

    override fun initUI() {
        setContentView(R.layout.activity_add_type)
        iv_back.visibility = View.VISIBLE
        ll_submit.visibility = View.VISIBLE
        tv_title.text = "添加预设时间"
        iv_back.setOnClickListener {
            finish()
        }
        isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            beanType = intent.getSerializableExtra("item_bean") as TypeDetailBean
            isImgNotChanged = beanType?.isDefault!!
        }
    }

    override fun initData() {
//        var type: BooleanArray = booleanArrayOf(false, false, false, true, true, true)
//        val pvTime = TimePickerBuilder(this, OnTimeSelectListener { date, v ->
//            //选中事件回调
//            ed_name.setText(date.time.toString())
//
//        })
//                .setType(type)//默认全部显示
//                .setLayoutRes(R.layout.pickerview_add_time, CustomListener() {
//                    fun customLayout(v: View) {
//
//                    }
//                })
//                .setContentTextSize(22)
//                .setLabel("", "", "", "", "", "")
//                .setLineSpacingMultiplier(1.2f)
//                .setTextXOffset(0, 0, 0, 30, 0, -30)
//                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
//                .setDecorView(fl_fragment)
//                .setBgColor(ContextCompat.getColor(this, R.color.color_94D1CA))
//                .setTextColorOut(0x33FFFFFF)
//                .setTextColorCenter(ContextCompat.getColor(this, R.color.white))
//                .isCyclic(true) //循环
//                .setOutSideCancelable(false)
//                .build()
//        pvTime.setKeyBackCancelable(false)
//        pvTime.show(false)

        getNoLinkData()
        val pvCustomOptions = OptionsPickerBuilder(this, OnOptionsSelectListener { options1, option2, options3, v ->
            //返回的分别是三个级别的选中位置
            selectTime = hour[options1] + ":" + minute[option2] + ":" + second[options3]
        }).setLayoutRes(R.layout.pickerview_add_time2, CustomListener() {
            fun customLayout(v: View) {

            }
        })
                .setContentTextSize(22)
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(30, 0, -20)
                .setDecorView(fl_fragment)
                .setBgColor(ContextCompat.getColor(this, R.color.color_94D1CA))
                .setTextColorOut(0x33FFFFFF)
                .setTextColorCenter(ContextCompat.getColor(this, R.color.white))
                .setCyclic(true, true, true)
                .setOutSideCancelable(false)
                .build<String>()

        pvCustomOptions.setNPicker(hour, minute, second)
        pvCustomOptions.setSelectOptions(0, 0, 0)
        pvCustomOptions.setKeyBackCancelable(false)
        pvCustomOptions.show(false)

        iv_img.setOnClickListener {
            val pop = PopupSelectPic(this)
            pop.listener = this@AddTypeActivity
            pop.showPopupWindow()
        }

        ll_submit.setOnClickListener {
            pvCustomOptions?.returnData()
            if (selectTime == "00:00:00") {
                toast("请选择时间")
                return@setOnClickListener
            }
            if (ed_name.text.isNullOrEmpty()) {
                toast("请输入菜单名称")
                return@setOnClickListener
            }
            if (!isEdit && isImgNotChanged == 1) {
                toast("请选择照片")
                return@setOnClickListener
            }
            intent.putExtra("item_time", TimeUtils.time2mill(selectTime))
            intent.putExtra("item_name", ed_name.text.toString())
            intent.putExtra("item_img", selectImg)
            intent.putExtra("item_is_default", isImgNotChanged)
            if (isEdit) {
                // 修改
                setResult(1, intent)
            } else {
                // 添加
                setResult(2, intent)
            }
            finish()
        }

        if (isEdit)
            initEditData(pvCustomOptions)
    }


    /**
     * "编辑"按钮进入时展示默认数据
     */
    private fun initEditData(pvCustomOptions: OptionsPickerView<*>) {
        ed_name.setText(beanType?.name)
        ed_name.setSelection(beanType?.name?.length!!)

        if (beanType?.isDefault == 1) {
            iv_img.setImageResource(beanType?.img!!.toInt())
        } else {
            Glide.with(this).load(beanType?.img).into(iv_img)
        }
        selectImg = beanType?.img!!

        val time = TimeUtils.mills2Time(beanType?.totalTime!!).split(":")
        val hour = time[0].toInt()
        val minute = time[1].toInt()
        val second = time[2].toInt()
        pvCustomOptions.setSelectOptions(hour, minute, second)

    }

    /**
     * 添加滚轮中自定义时间数字
     */
    private fun getNoLinkData() {
        // 小时00 -99
        for (i in 0..99) {
            if (i < 10) {
                hour.add("0" + i.toString())
            } else {
                hour.add(i.toString())
            }
        }
        // 分钟,秒  00-59
        for (i in 0..59) {
            if (i < 10) {
                minute.add("0" + i.toString())
                second.add("0" + i.toString())
            } else {
                minute.add(i.toString())
                second.add(i.toString())
            }
        }
    }

    /**
     * 选择相册
     */
    override fun onBtnPicClick() {
        initTakePhoto(1)
    }

    /**
     * 选择拍摄
     */
    override fun onBtnCameraClick() {
        initTakePhoto(2)
    }

    /**
     * 相片选择方式
     * @param tag: 1:相册   2:拍摄
     */
    private fun initTakePhoto(tag: Int) {
        val file = File(Environment.getExternalStorageDirectory(), "/Timer/" + System.currentTimeMillis() + ".jpg")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        val imageUri = Uri.fromFile(file)
//        configCompress(takePhoto)
        when (tag) {
            1 -> {
                getTakePhoto().onPickFromGalleryWithCrop(imageUri, getCropOptions())
            }
            2 -> {
                getTakePhoto().onPickFromCaptureWithCrop(imageUri, getCropOptions())
            }
        }
    }

    /**
     * 对相片进行裁剪
     * @return
     */
    private fun getCropOptions(): CropOptions? {
        val height = 300 //裁剪的长宽
        val width = 300
        val withWonCrop = false
        val builder = CropOptions.Builder()
        builder.setAspectX(width).setAspectY(height) // 宽/高
        builder.setWithOwnCrop(withWonCrop) //第三方裁剪工具
        return builder.create()
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    private fun getTakePhoto(): TakePhoto {
        if (takePhoto == null) {
            takePhoto = TakePhotoInvocationHandler.of(this).bind(TakePhotoImpl(this, this)) as TakePhoto
        }
        return takePhoto!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getTakePhoto().onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        getTakePhoto().onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 相机权限
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this)
    }

    /**
     * 展示图片
     */
    override fun takeSuccess(result: TResult?) {
        var images: ArrayList<TImage>? = result?.images
        val bitmapPath = File(images!![0].originalPath)
        if (bitmapPath.exists()) {
            // 图片存在直接展示
            Glide.with(this).load(bitmapPath).into(iv_img)
            selectImg = images[0].originalPath //图片设置成功才传递参数
            isImgNotChanged = 0  // 更改了图片(用于判断是否更改图片和是否默认数据)
        }
//        Glide.with(this).load(File(images!![0].originalPath))
//                .apply(RequestOptions().placeholder(R.drawable.icon_camera))
//                .into(iv_img)
    }

    override fun takeCancel() {
        Log.e(TAG, resources.getString(R.string.msg_operation_canceled))
    }

    override fun takeFail(result: TResult?, msg: String?) {
        Log.e(TAG, "takeFail:$msg")
    }

    /**
     * 权限检查
     */
    override fun invoke(invokeParam: InvokeParam?): PermissionManager.TPermissionType {
        val type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam?.method!!)
        if (PermissionManager.TPermissionType.WAIT == type) {
            this.invokeParam = invokeParam
        }
        return type
    }

}