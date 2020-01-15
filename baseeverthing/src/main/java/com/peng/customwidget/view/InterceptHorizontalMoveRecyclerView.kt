package com.peng.customwidget.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * 当手势横向移动距离大于纵向移动距离时，不拦截处理实践
 */
class InterceptHorizontalMoveRecyclerView : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var mDownX = 0f
    private var mDownY = 0f
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = event.rawX
                mDownY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                return abs(event.rawX - mDownX) < abs(event.rawY - mDownY)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

            }
        }
        return super.onInterceptTouchEvent(event)
    }
}