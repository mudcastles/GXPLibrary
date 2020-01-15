package com.example.brilonlinestudy.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.ScrollView

open class SmartScrollView : ScrollView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    // 初始化的时候设置一下值

    private var isScrolledToTop = true

    private var isScrolledToBottom = false

    private var mSmartScrollChangedListener: ISmartScrollChangedListener? = null

    /** 定义监听接口 */
    open interface ISmartScrollChangedListener {
        fun onScrolledToBottom()
        fun onScrolledToTop()
    }

    open fun setScanScrollChangedListener(smartScrollChangedListener: ISmartScrollChangedListener) {
        mSmartScrollChangedListener = smartScrollChangedListener
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        if (scrollY == 0) {
            isScrolledToTop = clampedY
            isScrolledToBottom = false
            Log.e("onOverScrolled isScrolledToTop:",isScrolledToTop.toString())
        } else {
            isScrolledToTop = false
            isScrolledToBottom = clampedY
            Log.e("onOverScrolled isScrolledToBottom:",isScrolledToBottom.toString())
        }
        notifyScrollChangedListeners()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        // 这个log可以研究ScrollView的上下padding对结果的影响
        Log.e("onScrollChanged getScrollY():", "$scrollY t: $t paddingTop: $paddingTop")

        if (android.os.Build.VERSION.SDK_INT < 9) {  // API 9及之后走onOverScrolled方法监听
            when {
                scrollY == 0 -> {    // 小心踩坑1: 这里不能是getScrollY() <= 0
                    isScrolledToTop = true
                    isScrolledToBottom = false
                    Log.e("onScrollChanged isScrolledToTop:",isScrolledToTop.toString())
                }
                scrollY + height - paddingTop - paddingBottom == getChildAt(0).height -> {
                    // 小心踩坑2: 这里不能是 >=
                    // 小心踩坑3（可能忽视的细节2）：这里最容易忽视的就是ScrollView上下的padding　
                    isScrolledToBottom = true
                    isScrolledToTop = false
                    Log.e("onScrollChanged isScrolledToBottom:",isScrolledToBottom.toString())
                }
                else -> {
                    isScrolledToTop = false
                    isScrolledToBottom = false
                }
            }
            notifyScrollChangedListeners()
        }
        // 有时候写代码习惯了，为了兼容一些边界奇葩情况，上面的代码就会写成<=,>=的情况，结果就出bug了
        // 我写的时候写成这样：getScrollY() + getHeight() >= getChildAt(0).getHeight()
        // 结果发现快滑动到底部但是还没到时，会发现上面的条件成立了，导致判断错误
        // 原因：getScrollY()值不是绝对靠谱的，它会超过边界值，但是它自己会恢复正确，导致上面的计算条件不成立
        // 仔细想想也感觉想得通，系统的ScrollView在处理滚动的时候动态计算那个scrollY的时候也会出现超过边界再修正的情况
    }

    private fun notifyScrollChangedListeners() {
        if (isScrolledToTop) {
            if (mSmartScrollChangedListener != null) {
                mSmartScrollChangedListener?.onScrolledToTop()
            }
        } else if (isScrolledToBottom) {
            if (mSmartScrollChangedListener != null) {
                mSmartScrollChangedListener?.onScrolledToBottom()
            }
        }
    }

    open fun isScrolledToTop(): Boolean {
        return isScrolledToTop
    }

    open fun isScrolledToBottom(): Boolean {
        return isScrolledToBottom
    }
}