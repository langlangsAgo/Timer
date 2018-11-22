package com.wonderelf.timer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.remair.util.FileUtils
import com.remair.util.ImageUtils
import com.wonderelf.timer.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Author: cl
 * Time: 2018/11/15
 * Description:本地图片操作类
 */
class SaveImageUtils {

    /**
     * 缓存drawable图片到本地SD卡
     */
    fun initPhoto(context: Context) {
        val bmpList = mutableListOf<Bitmap>()
        val bmp1 = BitmapFactory.decodeResource(context.resources, R.drawable.img_bean)
        val bmp2 = BitmapFactory.decodeResource(context.resources, R.drawable.img_cabbage)
        val bmp3 = BitmapFactory.decodeResource(context.resources, R.drawable.img_cattle)
        val bmp4 = BitmapFactory.decodeResource(context.resources, R.drawable.img_coriander)
        val bmp5 = BitmapFactory.decodeResource(context.resources, R.drawable.img_egg)
        val bmp6 = BitmapFactory.decodeResource(context.resources, R.drawable.img_intestine)
        val bmp7 = BitmapFactory.decodeResource(context.resources, R.drawable.img_lotusroot)
        val bmp8 = BitmapFactory.decodeResource(context.resources, R.drawable.img_mushroom)
        val bmp9 = BitmapFactory.decodeResource(context.resources, R.drawable.img_omasum)
        val bmp10 = BitmapFactory.decodeResource(context.resources, R.drawable.img_shrimp)
        val bmp11 = BitmapFactory.decodeResource(context.resources, R.drawable.img_squid)
        bmpList.add(bmp1)
        bmpList.add(bmp2)
        bmpList.add(bmp3)
        bmpList.add(bmp4)
        bmpList.add(bmp5)
        bmpList.add(bmp6)
        bmpList.add(bmp7)
        bmpList.add(bmp8)
        bmpList.add(bmp9)
        bmpList.add(bmp10)
        bmpList.add(bmp11)
        for (i in bmpList.indices) {
            val file = File(Environment.getExternalStorageDirectory(), "/Timer/img_default$i.jpg")
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                bmpList[i].compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    if (fos != null) {
                        fos.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun readImage(path: String){
        if (FileUtils.isFileExists(path)){
//            ImageUtils.getBitmap()
        }

    }
}