package com.wonderelf.timer.view;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.wonderelf.timer.R;
import com.wonderelf.timer.util.NotificationUtil;

import razerdp.basepopup.BasePopupWindow;

/**
 * Author: cl
 * Time: 2018/11/20
 * Description: 计时结束对话框
 */
public class PopupEndTime extends BasePopupWindow implements View.OnClickListener {

    private TextView tv_notify_name;
    private TextView btn_yes;
    private Context mContext;

    public PopupEndTime(Context context, String content) {
        super(context);
        mContext = context;
        tv_notify_name = (TextView) findViewById(R.id.tv_notify_name);
        btn_yes = (TextView) findViewById(R.id.btn_yes);

        tv_notify_name.setText(content + context.getResources().getText(R.string.end_time_tip));
        setViewClickListener(this, btn_yes);
        setAllowDismissWhenTouchOutside(true);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return getDefaultScaleAnimation();
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getDefaultScaleAnimation(false);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_endtime);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                dismiss();
//                if (listener != null){
//                    listener.onBtnYesClick();
//                }
                break;
            default:
                break;
        }
    }

    public void setListener(onItemOnClickListener listener) {
        this.listener = listener;
    }

    private onItemOnClickListener listener;

    public interface onItemOnClickListener {
        void onBtnYesClick();
    }

    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
//        // 如果通知关闭,弹出此框时播放铃声
//        boolean isEnalbe = NotificationUtil.isNotificationEnabled(mContext);
//        if (isEnalbe) {
//            // 判断channel
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationChannel channel = new NotificationUtil(mContext).getManager().getNotificationChannel(NotificationUtil.id);
//                if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
//                    NotificationUtil.playRingtone(mContext);
//                }
//            }
//        } else {
//            NotificationUtil.playRingtone(mContext);
//        }
        NotificationUtil.playRingtone(mContext);
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
        NotificationUtil.stopRingtone();
    }
}
