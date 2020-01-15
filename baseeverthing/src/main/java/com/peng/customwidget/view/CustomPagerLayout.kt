package com.peng.customwidget.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.viewpager.widget.ViewPager
import java.lang.IllegalArgumentException
import kotlin.math.abs
import kotlin.math.max

/**
 * 自己写的类似ViewPager效果的View
 * 为什么不直接用ViewPager？
 * 因为一个页面中ViewPager不能重名，否则不显示，放在RecyclerView中出问题了
 */
class CustomPagerLayout : FrameLayout {
    var mPagerSpace = 0 //相邻page之间的间隔
        set(value) {
            field = value
            requestLayout()
        }
    var mLeftPadding = 0    //第一个page左侧的padding
        set(value) {
            field = value
            requestLayout()
        }
    var mRightPadding = 0   //最后一个page右侧的padding
        set(value) {
            field = value
            requestLayout()
        }

    private var touchSlop = 0
    private var mContentWidth = 0   //内容部分总宽度
    private var mPagerWidth = 0
    private var currShowPage = 0    //当前显示的page
    private var mDownX = 0f
    private var mDownY = 0f
    private val pageCenterX = mutableListOf<Int>()  //记录每个page的中心X
    private var mScrollAnimator: ValueAnimator? = null

    private var mDownScrollX = 0
    private var mVisiableWidth = 0  //可是宽度

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        mVisiableWidth = measuredWidth
        mPagerWidth = measuredWidth - mLeftPadding - mRightPadding
        mContentWidth =
            mLeftPadding + mRightPadding + mPagerWidth * childCount
        if (childCount > 0) {
            mContentWidth += mPagerSpace * (childCount - 1)
        }

        var widthSpec = MeasureSpec.makeMeasureSpec(mPagerWidth, MeasureSpec.EXACTLY)
        var heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        var height = 0
        children.forEach {
            it.measure(widthSpec, heightSpec)
            height =
                max(height, (it.measuredHeight.toFloat() / it.measuredWidth * mPagerWidth).toInt())
        }
        setMeasuredDimension(mContentWidth, height)

        heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        children.forEach {
            it.measure(widthSpec, heightSpec)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        pageCenterX.clear()

        var currX = 0
        for (index in 0 until childCount) {
//            currX = 2 * mPagerSpace + (mPagerWidth + mPagerSpace) * index
            currX = mLeftPadding + (mPagerWidth + mPagerSpace) * index
            val child = getChildAt(index)
//            child.layout(
//                currX,
//                (measuredHeight - child.measuredHeight) / 2,
//                currX + mPagerWidth,
//                (measuredHeight + child.measuredHeight) / 2
//            )
            child.layout(
                currX,
                0,
                currX + mPagerWidth,
                measuredHeight
            )

            pageCenterX.add(currX + mPagerWidth / 2)
        }
    }


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (parent is ViewPager) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mDownX = event.rawX
                    mDownY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    if ((event.rawX > mDownX && isShowingLastPage()) || (event.rawX < mDownX && isShowingFirstPage())) {
                        parent.requestDisallowInterceptTouchEvent(false)
                        return false
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (mScrollAnimator != null && mScrollAnimator!!.isRunning) {
                    mScrollAnimator!!.removeAllUpdateListeners()
                    mScrollAnimator!!.cancel()
                }
                mDownX = event.rawX
                mDownY = event.rawY
                mDownScrollX = scrollX
            }
            MotionEvent.ACTION_MOVE -> {
                var scrollX = checkScrollX(event.rawX)
                this.scrollX = scrollX
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (abs(event.rawX - mDownX) < touchSlop && abs(event.rawY - mDownY) < touchSlop)
                    return super.onTouchEvent(event)
                val pageIndex = findClosedPageIndex()
                smoothScrollToPage(pageIndex)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun smoothScrollToPage(pageIndex: Int) {
        mScrollAnimator =
            ValueAnimator.ofInt(scrollX, pageCenterX[pageIndex] - mPagerWidth / 2 - mLeftPadding)
                .apply {
                    duration = 300
                    interpolator = DecelerateInterpolator()
                }
        if (!mScrollAnimator!!.isRunning) {
            mScrollAnimator!!.removeAllUpdateListeners()
            mScrollAnimator!!.addUpdateListener {
                scrollX = it.animatedValue as Int
            }
            mScrollAnimator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    currShowPage = pageIndex
                }
            })
            mScrollAnimator!!.start()
        }
    }

    private fun findClosedPageIndex(): Int {
        var closedPageIndex = 0
        var closedX = Int.MAX_VALUE
        pageCenterX.forEach {
            if (abs(scrollX + mVisiableWidth / 2 - it) < closedX) {
                closedX = abs(scrollX + mVisiableWidth / 2 - it)
                closedPageIndex = pageCenterX.indexOf(it)
            }
        }
        return closedPageIndex
    }

    private fun checkScrollX(rawX: Float): Int {
        val destX = mDownX - rawX + mDownScrollX
        return when {
            destX < 0 -> 0
            destX > (mContentWidth - mVisiableWidth) -> {
                mContentWidth - mVisiableWidth
            }
            else -> destX.toInt()
        }
    }

    fun getShowingPagePosition() = currShowPage
    fun isShowingLastPage() = currShowPage == childCount - 1
    fun isShowingFirstPage() = currShowPage == 0

    /**
     * 选中某一页
     */
    fun selectPage(position: Int) {
        if (position >= childCount) {
            throw IllegalArgumentException("position超出了可用范围!")
        }
        currShowPage = position
        scrollX = currShowPage * (mPagerWidth + mPagerSpace)
//        scrollX = mLeftPadding +
    }
}