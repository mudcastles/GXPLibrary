package com.peng.customwidget.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import com.peng.baseeverthing.R
import com.peng.baseeverthing.util.DimenUtil
import com.peng.baseeverthing.util.ScreenUtil
import kotlin.math.max


/**
 * 可以展开和折叠的悬浮按钮
 */
class AnimatorButton : View {
    private var animator: ValueAnimator? = null
    private var mOnButtonClickedListener: OnButtonClickedListener? = null
    private var mOnExpandListener: OnExpandListener? = null

    //每个buttonItem的宽高
    private var itemWidth: Int = 0
    private var itemHeight: Int = 0

    //屏幕宽高
    private val mScreenWidth = ScreenUtil.getScreenWidth(context)
    private val mScreenHeight = ScreenUtil.getScreenHeight(context)

    //动画状态
    private var mCurrAnimatorStatus: AnimatorStatus = AnimatorStatus.FOLDED
    //是否需要重新计算总宽高
    private var neddRecalMaxSize = true

    private var mCircleSizePx: Int = 0
    private var mPaint: Paint = Paint()
    private var mCircleSrc =
        DrawableCompat.wrap(ResourcesCompat.getDrawable(resources,R.drawable.ic_call_1, context.theme)!!).mutate()

    private var mTextSizePx = 0
    private var mTextMaxWidthPx = 0
    private var mTextMaxHeightPx = 0
    private var mDrawablePaddingPx = 0
    private var mTextPaddingVertical = 0
    private var mButtonItems = mutableListOf<ButtonItem>()

    private var foldProgress = 1.0f //折叠进度


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        mPaint.color = Color.RED
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.textSize = mTextSizePx.toFloat()
        mPaint.textAlign = Paint.Align.LEFT
    }

    /**
     * 设置圆的宽高
     */
    fun setCircleWidthHeight(dp: Float) {
        mCircleSizePx = DimenUtil.dip2px(context, dp)
        invalidate()
    }

    fun setTextSize(sp: Int) {
        mTextSizePx = DimenUtil.sp2px(context, sp.toFloat())
        mPaint.textSize = mTextSizePx.toFloat()
        neddRecalMaxSize = true
        invalidate()
    }

    fun setDrawablePadding(dp: Float) {
        mDrawablePaddingPx = DimenUtil.dip2px(context, dp)
        neddRecalMaxSize = true
        invalidate()
    }

    fun setTextPaddingVertical(dp: Float) {
        mTextPaddingVertical = DimenUtil.dip2px(context, dp)
        neddRecalMaxSize = true
        invalidate()
    }

    fun addButtonItem(item: ButtonItem) {
        mButtonItems.add(item)
        neddRecalMaxSize = true
        invalidate()
    }

    fun addButtonItems(vararg items: ButtonItem) {
        items.forEach {
            mButtonItems.add(it)
        }
        neddRecalMaxSize
        invalidate()
    }

    fun getStatus() = mCurrAnimatorStatus
    fun getExpandedWidth() = mTextMaxWidthPx
    fun getExpandedHeight() = mTextMaxHeightPx
    fun getFoldedWidth() = mCircleSizePx
    fun getFoldedHeight() = mCircleSizePx
    fun getOnButtonClickedListener() = mOnButtonClickedListener
    fun getOnExpandListener() = mOnExpandListener
    fun setOnButtonClickedListener(listener: OnButtonClickedListener) {
        this.mOnButtonClickedListener = listener
    }

    fun setOnExpandListener(listener: OnExpandListener) {
        this.mOnExpandListener = listener
    }


    open fun toggle() {
        if (mCurrAnimatorStatus == AnimatorStatus.RUNNING) return
        if (mCurrAnimatorStatus == AnimatorStatus.FOLDED) {
            mCurrAnimatorStatus = AnimatorStatus.RUNNING
            animator = ValueAnimator.ofFloat(1f, 0f)
                .setDuration(300)
            animator!!.addUpdateListener {
                foldProgress = it.animatedValue as Float
                mOnExpandListener?.onExpand(1 - foldProgress)
                requestLayout()
            }
            animator!!.interpolator = AccelerateDecelerateInterpolator()
            animator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    mCurrAnimatorStatus = AnimatorStatus.EXPAND
                    mOnExpandListener?.onExpand(1f)
                    requestLayout()
                }
            })
            animator!!.start()
        } else if (mCurrAnimatorStatus == AnimatorStatus.EXPAND) {
            mCurrAnimatorStatus = AnimatorStatus.RUNNING
            animator = ValueAnimator.ofFloat(0f, 1f)
                .setDuration(300)
            animator!!.addUpdateListener {
                foldProgress = it.animatedValue as Float
                mOnExpandListener?.onExpand(1 - foldProgress)
                requestLayout()
            }
            animator!!.interpolator = AccelerateDecelerateInterpolator()
            animator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    mCurrAnimatorStatus = AnimatorStatus.FOLDED
                    mOnExpandListener?.onExpand(0f)
                    requestLayout()
                }
            })
            animator!!.start()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (mCurrAnimatorStatus == AnimatorStatus.EXPAND) {
                val x = event.rawX - x
                mOnButtonClickedListener?.onButtonClicked(Math.floor((x / itemWidth).toDouble()).toInt())
            } else if (mCurrAnimatorStatus == AnimatorStatus.FOLDED) {
                toggle()
            }
            return true
        }
        return super.onTouchEvent(event)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        calMaxWidthHeight()
        if (mCurrAnimatorStatus == AnimatorStatus.FOLDED) {
            setMeasuredDimension(mCircleSizePx, mCircleSizePx)
        } else {
            val totalWidth = mTextMaxWidthPx - (mTextMaxWidthPx - mTextMaxHeightPx) * foldProgress
            setMeasuredDimension(totalWidth.toInt(), mTextMaxHeightPx)
        }
    }

    private fun calMaxWidthHeight() {
        //计算过一次后就不再计算了
        if (!neddRecalMaxSize) {
            return
        }

        var mRect = Rect()
        var mDrawableWidth = 0
        var mDrawableHeight = 0
        var mTextWidth = 0
        var mTextHeight = 0
        for (index in mButtonItems.indices) {
            val it = mButtonItems[index]
            mPaint.getTextBounds(it.text, 0, it.text.length, mRect)
            mDrawableHeight = it.drawable.intrinsicHeight
            mDrawableWidth = it.drawable.intrinsicWidth
            mTextWidth = mRect!!.right - mRect.left
            mTextHeight = mRect.bottom - mRect.top
            it.contentHeight = max(mDrawableHeight, mTextHeight)
            itemHeight = max(itemHeight, it.contentHeight)
            it.contentWidth = mDrawableWidth + mTextWidth + mDrawablePaddingPx
            itemWidth = max(itemWidth, it.contentWidth)
        }
        itemHeight += 2 * mTextPaddingVertical
        itemWidth += itemHeight

        mTextMaxHeightPx = itemHeight
        mTextMaxWidthPx =
            itemWidth * mButtonItems.size + mButtonItems.size - 1
        neddRecalMaxSize = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mCurrAnimatorStatus == AnimatorStatus.FOLDED) {
            mPaint.color = Color.WHITE
            mPaint.alpha = 255
            canvas.drawCircle(
                (measuredWidth / 2).toFloat(),
                (measuredHeight / 2).toFloat(),
                (measuredWidth / 2).toFloat(),
                mPaint
            )
            canvas.drawBitmap(
                drawableToBitmap(
                    mCircleSrc, ResourcesCompat.getColor(
                        context.resources,
                        R.color.tab_text_blue,
                        context.theme
                    )
                ),
                (measuredWidth - mCircleSrc.intrinsicWidth) / 2.toFloat(),
                (measuredHeight - mCircleSrc.intrinsicHeight) / 2.toFloat(),
                mPaint
            )
        } else {
            mPaint.color = ResourcesCompat.getColor(
                context.resources,
                R.color.tab_text_blue,
                context.theme
            )
            mPaint.alpha = 255
            canvas.drawRoundRect(
                0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(),
                (measuredHeight / 2).toFloat(), (measuredHeight / 2).toFloat(), mPaint
            )
            for (index in mButtonItems.indices) {
                val item = mButtonItems[index]
                var mDrawableWidth = item.drawable.intrinsicWidth

                val fontMetrics = mPaint.getFontMetrics()
                val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
                val baseline = measuredHeight / 2 + distance
                if (index == 0) {
                    mPaint.color = item.drawableColor
                    mPaint.alpha = ((1 - foldProgress) * 255).toInt()
                    val left =
                        ((itemWidth - item.contentWidth) / 2).toFloat()
                    canvas.drawBitmap(
                        drawableToBitmap(item.drawable, item.drawableColor),
                        left,
                        ((itemHeight - item.contentHeight) / 2).toFloat(),
                        mPaint
                    )
                    canvas.drawText(
                        item.text,
                        0,
                        item.text.length,
                        left + mDrawableWidth + mDrawablePaddingPx,
                        baseline,
                        mPaint
                    )
                }
                if (index == mButtonItems.size - 1) {
                    mPaint.color = item.drawableColor
                    mPaint.alpha = ((1 - foldProgress) * 255).toInt()
                    val left =
                        (measuredWidth - (itemWidth - item.contentWidth) / 2 - item.contentWidth).toFloat()
                    canvas.drawBitmap(
                        drawableToBitmap(item.drawable, item.drawableColor),
                        left,
                        ((itemHeight - item.contentHeight) / 2).toFloat(),
                        mPaint
                    )
                    canvas.drawText(
                        item.text,
                        0,
                        item.text.length,
                        left + mDrawableWidth + mDrawablePaddingPx,
                        baseline,
                        mPaint
                    )
                }
                if (index != 0 && index != mButtonItems.size - 1 && mCurrAnimatorStatus != AnimatorStatus.RUNNING) {
                    mPaint.color = item.drawableColor
                    mPaint.alpha = ((1 - foldProgress) * 255).toInt()
                    val left =
                        (itemWidth * index + index + (itemWidth - item.contentWidth) / 2).toFloat()
                    canvas.drawBitmap(
                        drawableToBitmap(item.drawable, item.drawableColor),
                        left,
                        ((itemHeight - item.contentHeight) / 2).toFloat(),
                        mPaint
                    )
                    canvas.drawText(
                        item.text,
                        0,
                        item.text.length,
                        left + mDrawableWidth + mDrawablePaddingPx,
                        baseline,
                        mPaint
                    )
                }

            }
            if (mCurrAnimatorStatus == AnimatorStatus.EXPAND) {
                mPaint.color = Color.WHITE
                mPaint.alpha = 255
                for (i in mButtonItems.indices) {
                    if (i == mButtonItems.size - 1) return
                    canvas.drawLine(
                        ((itemWidth + 1) * (i + 1)).toFloat(), 0f,
                        ((itemWidth + 1) * (i + 1) + 1).toFloat(), measuredHeight.toFloat(), mPaint
                    )
                }
            }
        }
    }

    /**
     * 改变透明通道的值
     * @param fraction [0,1]透明通道的值，0为全透明，1为不透明
     * @param color 需要改变的颜色值
     * @return 改变透明通道后的值，如：alphaEvaluator（0.5f,0xFFFFFFFF） = 0x80FFFFFF
     */
    private fun alphaEvaluator(fraction: Float, @ColorInt color: Int): Int {
        val r = (color shr 16) and 0xff
        val g = (color shr 8) and 0xff
        val b = color and 0xff

        return ((256 * fraction).toInt() shl 24) or (r shl 16) or (g shl 8) or b
    }


    // Drawable----> Bitmap
    private fun drawableToBitmap(drawable: Drawable, @ColorInt color: Int?): Bitmap {
        val drawableWrap = DrawableCompat.wrap(drawable).mutate()
        // 获取 drawable 长宽
        val width = drawableWrap.intrinsicWidth
        val heigh = drawableWrap.intrinsicHeight

        drawableWrap.setBounds(0, 0, width, heigh)

        if (color != null) {
            drawableWrap.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

        // 获取drawable的颜色格式
        val config = if (drawableWrap.opacity != PixelFormat.OPAQUE)
            Bitmap.Config.ARGB_8888
        else
            Bitmap.Config.RGB_565
        // 创建bitmap
        val bitmap = Bitmap.createBitmap(width, heigh, config)
        // 创建bitmap画布
        val canvas = Canvas(bitmap)
        // 将drawable 内容画到画布中
        drawableWrap.draw(canvas)
        return bitmap
    }


    class ButtonItem(var drawable: Drawable, var text: String, @ColorInt var drawableColor: Int) {
        var contentWidth = 0
        var contentHeight = 0
    }

    enum class AnimatorStatus {
        //折叠、展开、运行
        FOLDED,
        EXPAND, RUNNING
    }

    interface OnButtonClickedListener {
        fun onButtonClicked(index: Int)
    }

    interface OnExpandListener {
        fun onExpand(progress: Float)
    }
}