package com.peng.customwidget.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView

/**
 * 可以切换正在按压、将要松开、已经松开三种状态的ImageView
 * 常用于类似微信语音消息录音按钮（上滑取消发送、按压录音、抬起发送）
 */
class UpCancelImageView : ImageView {
    var mEventListener: OnEventListener? = null
    private var mCurrStatus =
        Status.STOP_RECORD
    private var mLocation = intArrayOf(0, 0)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        getLocationOnScreen(mLocation)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                mCurrStatus =
                    Status.RECORDING
                mEventListener?.onStartRecordVoice()
                mEventListener?.onStatusChanged(mCurrStatus)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val isOutBound = isOutBound(event.rawX, event.rawY)
                if (isOutBound && mCurrStatus != Status.WILL_CANCEL) {
                    mCurrStatus =
                        Status.WILL_CANCEL
                    mEventListener?.onStatusChanged(mCurrStatus)
                } else if(!isOutBound && mCurrStatus != Status.RECORDING){
                    mCurrStatus =
                        Status.RECORDING
                    mEventListener?.onStatusChanged(mCurrStatus)
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mCurrStatus =
                    Status.STOP_RECORD
                mEventListener?.onStatusChanged(mCurrStatus)
                if (isOutBound(event.rawX, event.rawY)) {
                    mEventListener?.onCancel()
                } else {
                    mEventListener?.onEndRecordVoice()
                }
                parent.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    internal fun isOutBound(x: Float, y: Float): Boolean {
        return x < mLocation[0] || x > mLocation[0] + measuredWidth || y < mLocation[1] || y > mLocation[1] + measuredHeight
    }

    interface OnEventListener {
        fun onCancel()
        fun onStartRecordVoice()
        fun onEndRecordVoice()
        fun onStatusChanged(newStatus: Status)
    }

    enum class Status {
        RECORDING, WILL_CANCEL, STOP_RECORD
    }
}
