package com.wonderelf.timer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.wonderelf.timer.R;
import com.wonderelf.timer.util.SelectorAttrs;


/**
 * 可在背景图和前景图显示遮罩效果的ImageView (前提设置了setClickable(true))
 *
 * @author huangziwei
 * @date 2015.12.29
 */
public class MaskImageView extends android.support.v7.widget.AppCompatImageView {

    private boolean mIsShowMaskOnClick = false; // 点击时是否显示遮罩，默认关闭
    private int mMaskColor; // 遮罩颜色（argb,需要设置透明度）

    ColorMatrix mColorMatrix = new ColorMatrix(); // 颜色矩阵
    ColorFilter mColorFilter;

    //总体大小
    private int mHeight;
    private int mWidth;
    //圆的半径
    private float mRadius;
    //圆心坐标
    private float x;
    private float y;
    //大圆颜色
    private int mTransparentColor;

    //实际百分比进度
    private int mPercent;
    //动画位置百分比进度
    private int mCurPercent;
    //要画的弧度
    private int mEndAngle;


    private Paint maskPaint; //遮罩层画笔
    private Paint bigCirclePaint; //中间大圆
    private Paint sectorPaint; //中间小圆
    private Paint mPaint;
    private Canvas canvasMask; //
    //设置一个Bitmap
    private Bitmap bitmap;

    public MaskImageView(Context context) {
        this(context, null);
    }

    public MaskImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.MaskImageView);
        mIsShowMaskOnClick = a.getBoolean(R.styleable.MaskImageView_miv_is_show_mask_on_click, mIsShowMaskOnClick);
        mMaskColor = a.getColor(R.styleable.MaskImageView_miv_mask_color, 0xB3000000);
        mTransparentColor = a.getColor(R.styleable.MaskImageView_miv_transColor, 0x00000000);
        mCurPercent = a.getInteger(R.styleable.MaskImageView_miv_percent, 0);
        a.recycle();
        //Bitmap的画笔
        mPaint = new Paint();
        //绘制大圆
        bigCirclePaint = new Paint();
        bigCirclePaint.setAntiAlias(true);
        bigCirclePaint.setColor(Color.GREEN);
        PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        bigCirclePaint.setXfermode(mode);
        // 绘制遮罩层
        maskPaint = new Paint();
        maskPaint.setAntiAlias(true);
        maskPaint.setColor(mMaskColor);

//        //饼状图
        sectorPaint = new Paint();
        sectorPaint.setColor(mMaskColor);
        sectorPaint.setAntiAlias(true);

    }

    private void setColorMatrix(float[] matrix) {
        mColorMatrix.set(matrix);
        mColorFilter = new ColorMatrixColorFilter(mColorMatrix);
    }

    // all drawables instances loaded from  the same resource share getScreenHeight common state
    // 从同一个资源文件获取的drawable对象共享一个状态信息，为了避免修改其中一个drawable导致其他drawable被影响，需要调用mutate()
    // 因为背景图在draw()阶段绘制，所以修改了背景图状态后必须调用invalidateSelf（）刷新
    private ColorFilter mLastColorFilter; // 记录上一次设置的filter避免重复设置导致递归调用ondraw

    private void setDrawableColorFilter(ColorFilter colorFilter) {
        if (getDrawable() != null) {
            if (mLastColorFilter == colorFilter) {
                return;
            }
            getDrawable().mutate();
            getDrawable().setColorFilter(colorFilter);
        }
        mLastColorFilter = colorFilter;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        //获取测量模式
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        //获取测量大小
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
//            mRadius = widthSize / 3;
//            x = widthSize / 2;
//            y = heightSize / 2;
//            mWidth = widthSize;
//            mHeight = heightSize;
//        }
//        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
//            mWidth = (int) (mRadius * 2);
//            mHeight = (int) (mRadius * 2);
//            x = mRadius;
//            y = mRadius;
//        }
        mWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        mHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);//设置宽和高
        //自己创建一个Bitmap
        bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        canvasMask = new Canvas(bitmap);//该画布为bitmap的
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsShowMaskOnClick) {
            //设置该View画布的背景
            canvas.drawBitmap(bitmap, 0, 0, mPaint);
            canvasMask.drawRect(0, 0, mWidth, mHeight, maskPaint);
            canvasMask.drawCircle(x, y, mRadius, bigCirclePaint);

            mEndAngle = (int) (mCurPercent * 3.6);
            RectF rect2 = new RectF(x - mRadius + 20, y - mRadius + 20, x + mRadius - 20, y + mRadius - 20);
            canvasMask.drawArc(rect2, -90, mEndAngle, true, sectorPaint);
        } else {
            setDrawableColorFilter(null);
        }

    }

    public boolean isIsShowMaskOnClick() {
        return mIsShowMaskOnClick;
    }

    public void setIsShowMaskOnClick(boolean isShowMaskOnClick) {
        this.mIsShowMaskOnClick = isShowMaskOnClick;
        invalidate();
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
                    MaskImageView.this.postInvalidate();
                }
            }

        }).start();
    }
}