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
 * Description: 弹出相册选择
 */
public class PopupSelectPic extends BasePopupWindow implements View.OnClickListener {

    private TextView btn_pic;
    private TextView btn_camera;
    private TextView cancel;

    public PopupSelectPic(Context context) {
        super(context);
        btn_pic = (TextView) findViewById(R.id.btn_pic);
        btn_camera = (TextView) findViewById(R.id.btn_camera);
        cancel = (TextView) findViewById(R.id.cancel);

        setViewClickListener(this, btn_pic, btn_camera, cancel);
        setAllowDismissWhenTouchOutside(true);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return getTranslateVerticalAnimation(1f, 0f, 300);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getTranslateVerticalAnimation(0f, 1f, 300);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_select_pic);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pic:
                dismiss();
                if (listener != null){
                    listener.onBtnPicClick();
                }
                break;
            case R.id.btn_camera:
                dismiss();
                if (listener != null){
                    listener.onBtnCameraClick();
                }
                break;
            case R.id.cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    public onSelectOnClickListener getListener() {
        return listener;
    }

    public void setListener(onSelectOnClickListener listener) {
        this.listener = listener;
    }

    private onSelectOnClickListener listener;

    public interface onSelectOnClickListener{
        void onBtnPicClick();
        void onBtnCameraClick();
    }

}
