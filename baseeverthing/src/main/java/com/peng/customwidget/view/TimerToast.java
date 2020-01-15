package com.peng.customwidget.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.peng.baseeverthing.util.ScreenUtil;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 自定义显示时长的Toast
 */
public class TimerToast extends PopupWindow {
    private Context context;
    public static final int TOAST_LENGTH_INFINITE = -1;
    private Timer timer;

    public TimerToast(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void setContentView(View contentView) {
        super.setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(false);
        setOutsideTouchable(true);
        update();
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        contentView.measure(spec, spec);
    }

    public void show(View view) {
        show(view, TOAST_LENGTH_INFINITE);
    }

    /***
     *
     * @param view 目标view
     * @param toastLength 显示时长，毫秒
     */
    public void show(final View view, int toastLength) {
        if (toastLength == TOAST_LENGTH_INFINITE)
            showAtLocation(view, Gravity.NO_GRAVITY, (ScreenUtil.INSTANCE.getScreenWidth(context) - getContentView().getMeasuredWidth()) / 2, (ScreenUtil.INSTANCE.getScreenHeight(context) - getContentView().getMeasuredHeight()) / 2);
        else if (toastLength > 0) {
            if (timer != null) {
                timer.cancel();
                dismiss();
            }
            showAtLocation(view, Gravity.NO_GRAVITY, (ScreenUtil.INSTANCE.getScreenWidth(context) - getContentView().getMeasuredWidth()) / 2, (ScreenUtil.INSTANCE.getScreenHeight(context) - getContentView().getMeasuredHeight()) / 2);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    dismiss();
                }
            },0,toastLength);
        } else {
            throw new IllegalArgumentException("toastLength非法");
        }
    }

}
