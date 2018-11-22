package com.wonderelf.timer.view;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.wonderelf.timer.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * Author: cl
 * Time: 2018/11/13
 * Description: 确认删除对话框
 */
public class PopupDelType extends BasePopupWindow implements View.OnClickListener {

    private TextView tv_title;
    private TextView btn_no;
    private TextView btn_yes;

    public PopupDelType(Context context) {
        super(context);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_no = (TextView) findViewById(R.id.btn_no);
        btn_yes = (TextView) findViewById(R.id.btn_yes);

        setViewClickListener(this, btn_no, btn_yes);
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
        return createPopupById(R.layout.pop_delete);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_no:
                dismiss();
                if (listener != null){
                    listener.onBtnNoClick();
                }
                break;
            case R.id.btn_yes:
                dismiss();
                if (listener != null){
                    listener.onBtnYesClick();
                }
                break;
            default:
                break;
        }
    }

    public onItemOnClickListener getListener() {
        return listener;
    }

    public void setListener(onItemOnClickListener listener) {
        this.listener = listener;
    }

    private onItemOnClickListener listener;

    public interface onItemOnClickListener{
        void onBtnNoClick();
        void onBtnYesClick();
    }

}
