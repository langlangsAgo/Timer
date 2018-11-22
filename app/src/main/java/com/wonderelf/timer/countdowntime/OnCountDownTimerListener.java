package com.wonderelf.timer.countdowntime;

/**
 * Author: cl
 * Time: 2018/11/9
 * Description: 倒计时监听
 */
public interface OnCountDownTimerListener{
    void onTick(long millisUntilFinished);

    void onFinish();
}
