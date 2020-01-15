package com.peng.customwidget.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * 根据子View的高度自适应高度的ViewPager
 */
class WrapHeightViewPager : ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val h = child.measuredHeight
            if (h > height) height = h
        }

        val myHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        super.onMeasure(widthMeasureSpec, myHeightMeasureSpec)
    }
}