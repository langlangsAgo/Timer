package com.wonderelf.timer.countdowntime;

/**
 * Author: cl
 * Time: 2018/11/9
 * Description: 倒计时方法
 */
public interface ITimerSupport {
    void start();

    void pause();

    void resume();

    void stop();

    void reset();
}
