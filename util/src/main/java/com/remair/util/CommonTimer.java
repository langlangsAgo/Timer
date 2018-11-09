package com.remair.util;

import android.os.CountDownTimer;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2017/5/24.
 */

public class CommonTimer extends CountDownTimer {
    public static final int gAction_Tick = 1;
    public static final int gAction_Finish = 2;

    private static Map<String, List<SoftReference<CommonTimer>>> goTimerList = new HashMap<String, List<SoftReference<CommonTimer>>>();

    public static void pauseTimer(String astrClassName, boolean abPause) {
        List<SoftReference<CommonTimer>> loTimerList = goTimerList.get(astrClassName);
        if (loTimerList == null) {
            return;
        }
        for (int i = 0; i < loTimerList.size(); ++i) {
            if (loTimerList.get(i) == null || loTimerList.get(i).get() == null) {
                loTimerList.remove(i);
                break;
            }
            if (abPause)
                loTimerList.get(i).get().pause();
            else
                loTimerList.get(i).get().resume();
        }
    }

//	public static void Cancel(String astrClassName)
//	{
//
//	}

    public static void remove(String astrClassName, CommonTimer aoTimer) {
        List<SoftReference<CommonTimer>> loTimerList = goTimerList.get(astrClassName);
        if (loTimerList == null) {
            return;
        }
        for (int i = 0; i < loTimerList.size(); ++i) {
            if (loTimerList.get(i).get() == aoTimer) {
                loTimerList.get(i).get().moHandler = null;
                loTimerList.remove(i);
                return;
            }
        }
    }

    private static void AddTimer(String astrClassName, CommonTimer aoTimer) {
        List<SoftReference<CommonTimer>> loTimerList = goTimerList.get(astrClassName);
        if (loTimerList == null) {
            loTimerList = new ArrayList<SoftReference<CommonTimer>>();
            goTimerList.put(astrClassName, loTimerList);
        }
        for (int i = 0; i < loTimerList.size(); ++i) {
            if (loTimerList.get(i).get() == aoTimer)
                return;
        }

        loTimerList.add(new SoftReference(aoTimer));
    }

    public static abstract class TimerDelegation {
        public abstract boolean TimerHandler(long alMill, int aiEvent);
    }

    public CommonTimer(long millisInFuture, long countDownInterval, String astrClassName, TimerDelegation aoHandler) {
        super(millisInFuture, countDownInterval);
        // TODO Auto-generated constructor stub
        moHandler = aoHandler;
        mstrClassName = astrClassName;
        AddTimer(mstrClassName, this);
    }

    private boolean mbStart = false;
    private boolean mbPause = false;
    private String mstrClassName = "";

    public void pause() {
        mbPause = true;
    }

    public void resume() {
        mbPause = false;
    }

    private TimerDelegation moHandler = null;

    public void finish() {
        this.cancel();
        remove(mstrClassName, this);
    }

    @Override
    public void onFinish() {
        // TODO Auto-generated method stub
        excuteHandler(gAction_Finish, 0);
        remove(mstrClassName, this);
    }

    private void excuteHandler(int astrAction, long llMill) {
        if (moHandler == null) {
            this.finish();
            return;
        }

        if (moHandler.TimerHandler(llMill, astrAction) == false) {
            this.finish();
            return;
        }
    }

    @Override
    public void onTick(long millisUntilFinished) {
        // TODO Auto-generated method stub
        if (mbPause)
            return;
//		long lltime1 = System.currentTimeMillis();
        excuteHandler(gAction_Tick, millisUntilFinished);

//		LogUtil.d("onTick cost "+ (System.currentTimeMillis()-lltime1));
    }

    public String getClassName() {
        return mstrClassName;
    }
}
