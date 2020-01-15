package com.peng.customwidget.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.peng.baseeverthing.R

/**
 * title、subtitle居中的toolbar
 */
class CenterTextToolBar : Toolbar {
    /** title、subtitle是否居中 */
    var isTitleCenter = true
        set(value) {
            field = value
            requestLayout()
        }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        val ta = context!!.obtainStyledAttributes(attrs, R.styleable.CenterTextToolBar, defStyleAttr, 0)
        isTitleCenter = ta.getBoolean(R.styleable.CenterTextToolBar_text_center,true)
        ta.recycle()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (isTitleCenter) {
            for (index in 0 until childCount) {
                val view = getChildAt(index)
                if (view is TextView) {
                    view.layout(
                        (measuredWidth - view.measuredWidth) / 2,
                        view.y.toInt(),
                        (measuredWidth + view.measuredWidth) / 2,
                        (view.y + view.measuredHeight).toInt()
                    )
                }
            }
        }
    }


}