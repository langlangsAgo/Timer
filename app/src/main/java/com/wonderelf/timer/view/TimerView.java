package com.wonderelf.timer.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.remair.util.LogUtils;
import com.wonderelf.timer.R;
import com.wonderelf.timer.util.ImageUtils;

/**
 * Author: cl
 * Time: 2018/11/8
 * Description:一个圆形百分比进度 View
 * 用于展示简易的图标
 */
public class TimerView extends android.support.v7.widget.AppCompatImageView {

    private boolean mIsShowMaskOnClick = false; // 点击时是否显示遮罩，默认关闭
    private boolean isFirst = true; // 是否第一次展示
    private boolean isStart = true;  // 是否开始动画,第一次进入开始, 开始后false,动画结束再设置为true

    private int mHeight;
    private int mWidth;
    //实际百分比进度
    private int mPercent;
    //动画位置百分比进度
    private int mCurPercent;

    private float mRoundRadius = 0; // 矩形的圆角半径,默认为０，即直角矩形
    private float mRoundRadiusLeftTop, mRoundRadiusLeftBottom, mRoundRadiusRightTop, mRoundRadiusRightBottom;
    private int mMaskColor; // 遮罩颜色（argb,需要设置透明度）

    private final Matrix mShaderMatrix = new Matrix();
    private Bitmap mBitmap;
    private Paint mBitmapPaint = new Paint();
    private BitmapShader mBitmapShader;
    private Path mPath = new Path();
    private RectF mViewRect = new RectF(); // imageview的矩形区域


    //圆心坐标
    private float x;
    private float y;
    private float mRadius; //圆的半径

    private Paint bigCirclePaint; //中间大圆
    private Paint maskPaint; //遮罩层
    private Paint paint; // 扇形

    private Paint mPaint;
    private Canvas canvasMask; //
    private Bitmap bitmap;  //设置一个Bitmap

    private float startAngle; // 起始角度
    private float sweepAngle; // 当前的角度
    private float currentProgress; // 当前的进度 0-100
    private long mCountdownTime; // 时长


    public TimerView(Context context) {
        this(context, null);
    }

    public TimerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.TimerView);
        try {
            mRoundRadius = a.getDimension(R.styleable.TimerView_siv_round_radius, mRoundRadius);
            mRoundRadiusLeftBottom = a.getDimension(R.styleable.TimerView_siv_round_radius_leftBottom, mRoundRadius);
            mRoundRadiusLeftTop = a.getDimension(R.styleable.TimerView_siv_round_radius_leftTop, mRoundRadius);
            mRoundRadiusRightBottom = a.getDimension(R.styleable.TimerView_siv_round_radius_rightBottom, mRoundRadius);
            mRoundRadiusRightTop = a.getDimension(R.styleable.TimerView_siv_round_radius_rightTop, mRoundRadius);

            mMaskColor = a.getColor(R.styleable.TimerView_siv_mask_color, 0xB3000000);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
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

        // 中间扇形
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mMaskColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        mHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        x = mWidth / 2;
        y = mHeight / 2;
        mRadius = mWidth / 3;
        setMeasuredDimension(mWidth, mHeight);//设置宽和高
        //自己创建一个Bitmap
        bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        canvasMask = new Canvas(bitmap);//该画布为bitmap的
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = ImageUtils.getBitmapFromDrawable(getDrawable());
        setupBitmapShader();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = ImageUtils.getBitmapFromDrawable(drawable);
        setupBitmapShader();
    }

    private void setupBitmapShader() {
        // super(context, attrs, defStyle)调用setImageDrawable时,成员变量还未被正确初始化
        if (mBitmapPaint == null) {
            return;
        }
        if (mBitmap == null) {
            invalidate();
            return;
        }
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint.setShader(mBitmapShader);

        // 固定为CENTER_CROP,使图片在ｖｉｅｗ中居中并裁剪
        mShaderMatrix.set(null);
        // 缩放到高或宽　与view的高或宽　匹配
        float scale = Math.max(getWidth() * 1f / mBitmap.getWidth(), getHeight() * 1f / mBitmap.getHeight());
        // 由于BitmapShader默认是从画布的左上角开始绘制，所以把其平移到画布中间，即居中
        float dx = (getWidth() - mBitmap.getWidth() * scale) / 2;
        float dy = (getHeight() - mBitmap.getHeight() * scale) / 2;
        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate(dx, dy);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mBitmap != null) {
            mPath.reset();
            mPath.addRoundRect(mViewRect, new float[]{
                    mRoundRadiusLeftTop, mRoundRadiusLeftTop,
                    mRoundRadiusRightTop, mRoundRadiusRightTop,
                    mRoundRadiusRightBottom, mRoundRadiusRightBottom,
                    mRoundRadiusLeftBottom, mRoundRadiusLeftBottom,
            }, Path.Direction.CW);
            canvas.drawPath(mPath, mBitmapPaint);
        }

        if (mIsShowMaskOnClick) {
            //设置该View画布的背景
//            if (isFirst) {
//                LogUtils.e("-----------画背景");
//                isFirst = false;
//                canvas.drawBitmap(bitmap, 0, 0, mPaint);
//                //设置该View画布的背景
//                canvasMask.drawRoundRect(new RectF(0, 0, mWidth, mHeight), 10, 10, maskPaint);
//                canvasMask.drawCircle(x, y, mRadius, bigCirclePaint);
//            }

            // 画扇形
            RectF oval = new RectF(x - mRadius + 10, y - mRadius + 10, x + mRadius - 10, y + mRadius - 10);
            canvas.drawArc(oval, startAngle, sweepAngle, true, paint);

        } else {
            setDrawableColorFilter(null);
        }
    }

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

    public boolean isIsShowMaskOnClick() {
        return mIsShowMaskOnClick;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initRect();
        setupBitmapShader();
    }

    //　设置图片的绘制区域
    private void initRect() {
        mViewRect.top = 0;
        mViewRect.left = 0;
        mViewRect.right = getWidth(); // 宽度
        mViewRect.bottom = getHeight(); // 高度
    }

    // 设置圆角
    public void setRoundRadius(float mRoundRadius) {
        this.mRoundRadius = mRoundRadius;
        invalidate();
    }

    // 设置四个角圆角
    public void setRoundRadiis(float roundRadiusLeftBottom, float roundRadiusLeftTop, float roundRadiusRightBottom, float roundRadiusRightTop) {
        mRoundRadiusLeftBottom = roundRadiusLeftBottom;
        mRoundRadiusLeftTop = roundRadiusLeftTop;
        mRoundRadiusRightBottom = roundRadiusRightBottom;
        mRoundRadiusRightTop = roundRadiusRightTop;
        invalidate();
    }

    private ValueAnimator animator;
    // 开始动画
    public void startCountDownTime() {
//        if (animator.isStarted()){
//            animator.end();
//        }
        animator = ValueAnimator.ofFloat(0, 100);
        animator.setDuration(mCountdownTime);
        animator.setInterpolator(new LinearInterpolator());//匀速
        animator.setRepeatCount(0);
        //值从0-1.0F 的动画，动画时长为countdownTime，ValueAnimator没有跟任何的控件相关联，那也正好说明ValueAnimator只是对值做动画运算，而不是针对控件的，我们需要监听ValueAnimator的动画过程来自己对控件做操作
        //添加监听器,监听动画过程中值的实时变化(animation.getAnimatedValue()得到的值就是0-1.0)
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                /**
                 * 这里我们已经知道ValueAnimator只是对值做动画运算，而不是针对控件的，因为我们设置的区间值为0-1.0f
                 * 所以animation.getAnimatedValue()得到的值也是在[0.0-1.0]区间，而我们在画进度条弧度时，设置的当前角度为360*currentAngle，
                 * 因此，当我们的区间值变为1.0的时候弧度刚好转了360度
                 */
                currentProgress = Float.valueOf(String.valueOf(animation.getAnimatedValue()));
                float degree = (int) (360 * (currentProgress / 100f));
                updateArgs(degree);
                invalidate();//实时刷新view，这样我们的进度条弧度就动起来了
            }
        });
        //开启动画
        animator.start();
        //还需要另一个监听，监听动画状态的监听器
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsShowMaskOnClick = false;
                isFirst = true;
                isStart = true;
                invalidate();
                if (animator != null) {
                    animator.cancel();
                    animator = null;
                }
            }
        });
    }

    private void updateArgs(float degree) {
        startAngle = degree + 270f;
        if (startAngle >= 360f) {
            startAngle -= 360f;
            sweepAngle = 270f - startAngle;
        } else {
            sweepAngle = 360f - degree;
        }
    }

    /**
     * 设置倒计时长,并开始动画
     *
     * @param mCountdownTime
     */
    public void setCountdownTime(long mCountdownTime, boolean isShowMaskOnClick) {
        this.mCountdownTime = mCountdownTime;
        this.mIsShowMaskOnClick = isShowMaskOnClick;
        if (mIsShowMaskOnClick && isStart) {
            isStart = false;
            startCountDownTime();
        }
    }

    boolean isAnimatorPause = false;

    /**
     * 暂停动画
     */
    public void setAnimatorPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            animator.pause();
        } else {
            if (!isAnimatorPause) {
                animator.cancel();
                isAnimatorPause = true;
            }
        }
    }

    /**
     * 继续动画
     */
    public void setAnimatorResume(long duration){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            animator.resume();
        }else {
            animator = ValueAnimator.ofFloat(currentProgress, 100);
            animator.setDuration(duration);
            animator.setInterpolator(new LinearInterpolator());//匀速
            animator.setRepeatCount(0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    /**
                     * 这里我们已经知道ValueAnimator只是对值做动画运算，而不是针对控件的，因为我们设置的区间值为0-1.0f
                     * 所以animation.getAnimatedValue()得到的值也是在[0.0-1.0]区间，而我们在画进度条弧度时，设置的当前角度为360*currentAngle，
                     * 因此，当我们的区间值变为1.0的时候弧度刚好转了360度
                     */
                    currentProgress = Float.valueOf(String.valueOf(animation.getAnimatedValue()));
                    float degree = (int) (360 * (currentProgress / 100f));
                    updateArgs(degree);
                    invalidate();//实时刷新view，这样我们的进度条弧度就动起来了
                }
            });
            //还需要另一个监听，监听动画状态的监听器
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mIsShowMaskOnClick = false;
                    isFirst = true;
                    isStart = true;
                    invalidate();
                }
            });
            animator.start();
        }

    }
}
