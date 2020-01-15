package com.peng.customwidget.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class SizeChangedLinearLayout : LinearLayout {

    private var onSizeChangedListener: OnSizeChangedListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onSizeChangedListener?.onSizeChanged(w, h, oldw, oldh)
    }

    fun setOnSizeChangedListener(onSizeChangedListener: OnSizeChangedListener){
        this.onSizeChangedListener = onSizeChangedListener
    }
    interface OnSizeChangedListener {
        fun onSizeChanged(newWidth: Int, newHeight: Int, oldWidth: Int, oldHeight: Int)
    }
}