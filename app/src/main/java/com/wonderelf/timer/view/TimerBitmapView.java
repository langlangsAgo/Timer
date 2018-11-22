package com.wonderelf.timer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wonderelf.timer.R;

/**
 * Author: cl
 * Time: 2018/11/9
 * Description:
 */
public class TimerBitmapView extends View {

    private int width;//设置高
    private int height;//设置高
    private Paint mPaint;

    //设置一个Bitmap
    private Bitmap bitmap;
    //创建该Bitmap的画布
    private Canvas bitmapCanvas;
    private Paint mPaintCover;
    private Paint mPaintRect;

    //定义一样个背景的Bitmap
    private Bitmap mBitmapBackground;
    private Matrix matrix;
    private Path mPath;

    //实际百分比进度
    private int mPercent;
    //动画位置百分比进度
    private int mCurPercent;
    //要画的弧度
    private int mEndAngle;

    public TimerBitmapView(Context context) {
        super(context);
    }

    public TimerBitmapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerBitmapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();//Bitmap的画笔

        //设置背景
        mBitmapBackground = BitmapFactory.decodeResource(getResources(), R.drawable.img_cabbage);

        mPaintCover = new Paint();
        mPaintCover.setAntiAlias(true);
        mPaintCover.setColor(Color.GRAY);
        mPaintCover.setStrokeWidth(50);
        //设置图形混合方式，这里使用PorterDuff.Mode.XOR模式，与底层重叠部分设为透明
        PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
        mPaintCover.setXfermode(mode);
        mPaintCover.setStyle(Paint.Style.STROKE);
        //设置笔刷的样式，默认为BUTT，如果设置为ROUND(圆形),SQUARE(方形)，需要将填充类型Style设置为STROKE或者FILL_AND_STROKE
        mPaintCover.setStrokeCap(Paint.Cap.ROUND);
        //设置画笔的结合方式
        mPaintCover.setStrokeJoin(Paint.Join.ROUND);

        //绘制蒙版的画笔
        mPaintRect = new Paint();
        mPaintRect.setAntiAlias(true);
        mPaintRect.setColor(Color.LTGRAY);

        //路径记录滑动屏幕的路径。
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);//设置宽和高

        //创建一个Bitmap，用于绘图。
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);//该画布为bitmap。

        //绘制背景BitmapBackground大小的矩阵
        matrix = new Matrix();//如果在构造器中初始化，需要使用reset()方法
        matrix.postScale((float) width / mBitmapBackground.getWidth(), (float) height / mBitmapBackground.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将bitmapBackground设置该View画布的背景
        canvas.drawBitmap(mBitmapBackground, matrix, null);
        //然后画布添加背景的基础上添加bitmap。
        canvas.drawBitmap(bitmap, 0, 0, mPaint);
        bitmapCanvas.drawRect(0, 0, width, height, mPaintRect);//bitmap上绘制一个蒙版
        bitmapCanvas.drawPath(mPath, mPaintCover);//bitmap上绘制手 划过的路径
    }

    //这里设置初始值是为了不点击屏幕时 ，不显示路径
    private float down_x = -100;
    private float down_y = -100;
    private float move_x = -100;
    private float move_y = -100;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获得点击屏幕时的坐标
                down_x = event.getX();
                down_y = event.getY();
                //将Path移动到点击点
                mPath.moveTo(down_x, down_y);
                invalidate();//更新画面
                return true;
            case MotionEvent.ACTION_MOVE:
                //获得在屏幕上移动的坐标
                move_x = event.getX();
                move_y = event.getY();
                //将移动的轨迹画成直线
                mPath.lineTo(move_x, move_y);
                //然后移动到下一个点。
                mPath.moveTo(move_x, move_y);
                invalidate();//更新画面
                return true;
        }
        return super.onTouchEvent(event);
    }

    //外部设置百分比数
    public void setPercent(int percent) {
        if (percent > 100) {
            throw new IllegalArgumentException("percent must less than 100!");
        }

        setCurPercent(percent);


    }

    //内部设置百分比 用于动画效果
    private void setCurPercent(int percent) {

        mPercent = percent;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int sleepTime = 1;
                for (int i = 0; i < mPercent; i++) {
                    if (i % 20 == 0) {
                        sleepTime += 2;
                    }
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mCurPercent = i;
                    TimerBitmapView.this.postInvalidate();
                }
            }

        }).start();
    }
}
