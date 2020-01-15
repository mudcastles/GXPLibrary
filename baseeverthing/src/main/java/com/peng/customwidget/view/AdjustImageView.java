package com.peng.customwidget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.peng.baseeverthing.R;

/**
 * 可以按照宽（高）来自动适应高（宽）的ImageView
 */
public class AdjustImageView extends AppCompatImageView {
    public static final int WIDTH = 0;
    public static final int HEIGHT = 1;
    private int adjustForWhat = WIDTH;

    public AdjustImageView(Context context) {
        super(context);
    }

    public AdjustImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AdjustImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray ar = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AdjustImageView, defStyleAttr, 0);
            adjustForWhat = ar.getInteger(R.styleable.AdjustImageView_adjust_for, WIDTH);
            ar.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            if (adjustForWhat == WIDTH) {
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = (int) Math.ceil((float) width * (float) drawable.getIntrinsicHeight() / (float) drawable.getIntrinsicWidth());
                setMeasuredDimension(width, height);
            }else{
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                int width = (int) Math.ceil((float) height * (float) drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight());
                setMeasuredDimension(width, height);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
