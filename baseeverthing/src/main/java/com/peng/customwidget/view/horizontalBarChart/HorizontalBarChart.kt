package com.peng.customwidget.view.horizontalBarChart

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.peng.baseeverthing.R
import com.peng.baseeverthing.util.DimenUtil
import kotlin.math.max

/**
 * 横向柱状图
 */
class HorizontalBarChart : View {
    private val mBarChartData = mutableListOf<BarChartData>()
    var mItemSpacePx = 0f    //item之间的间隔 px
        set(value) {
            field = value
            requestLayout()
        }
    var mTextDrawableSpacePx = 0f    //文字与柱图之间的间隔 px
        set(value) {
            field = value
            requestLayout()
        }
    var mValueDrawableSpacePx = 0f   //数字与柱图之间的间隔 px
        set(value) {
            field = value
            requestLayout()
        }
    var mTextSizePx = 0f   //文字大小 px
        set(value) {
            field = value
            requestLayout()
        }
    var mValueSizePx = 0f   //数字大小 px
        set(value) {
            field = value
            requestLayout()
        }
    var mTextColor: Int? = null
        set(value) {
            field = value
            invalidate()
        }
    var mValueColor: Int? = null
        set(value) {
            field = value
            invalidate()
        }
    var mDrawable: Drawable? = null //柱图的drawbale
        set(value) {
            field = value
            invalidate()
        }


    private var mMaxLengthText: String = "" //最长的文字
    private var mMaxValue = 0   //最大的数字

    private var mTextWidth = 0  //文字部分的宽度
    private var mValueWidth = 0 //数字部分的宽度
    private var mDrawableWidth = 0  //柱图的宽度
    private var mItemHeight = 0 //item的高度
    private var mContentHeight = 0  //内容部分的高度

    private val mPaint = Paint()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        init(context, attrs, defStyleAttr, 0)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
       init(context, attrs, defStyleAttr, defStyleRes)
    }
    private fun init( context: Context,
                      attrs: AttributeSet?,
                      defStyleAttr: Int,
                      defStyleRes: Int){
        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.HorizontalBarChart,
            defStyleAttr,
            defStyleRes
        )
        mItemSpacePx = ta.getDimension(R.styleable.HorizontalBarChart_itemSpace, 0f)
        mTextDrawableSpacePx =
            ta.getDimension(R.styleable.HorizontalBarChart_textDrawableSpace, 0f)
        mValueDrawableSpacePx =
            ta.getDimension(R.styleable.HorizontalBarChart_valueDrawableSpace, 0f)
        mTextSizePx =
            ta.getDimension(
                R.styleable.HorizontalBarChart_charTextSize,
                DimenUtil.sp2px(context, 12f).toFloat()
            )
        mValueSizePx =
            ta.getDimension(
                R.styleable.HorizontalBarChart_charTextSize,
                DimenUtil.sp2px(context, 8f).toFloat()
            )
        mDrawable = ta.getDrawable(R.styleable.HorizontalBarChart_itemDrawable)
        mTextColor = ta.getColor(R.styleable.HorizontalBarChart_textColor, Color.WHITE)
        mValueColor =
            ta.getColor(R.styleable.HorizontalBarChart_valueColor, Color.parseColor("#00b8e1"))
        ta.recycle()
        if (mDrawable == null) {
            mDrawable = ColorDrawable(Color.parseColor("#ff00c0e9"))
//            mDrawable = ColorDrawable(0xff00c0e9.toInt())
        }

        initPaint()
    }

    private fun initPaint() {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.textAlign = Paint.Align.LEFT
    }


    fun addBarChartData(data: BarChartData) {
        this.mBarChartData.add(data)
        requestLayout()
    }

    fun addBarChartData(datas: List<BarChartData>) {
        this.mBarChartData.clear()
        this.mBarChartData.addAll(datas)
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mBarChartData.forEach {
            if (it.text.length > mMaxLengthText.length) mMaxLengthText = it.text
            if (it.value > mMaxValue) mMaxValue = it.value
        }
        //计算文字宽度高度
        mPaint.textSize = mTextSizePx
        val textRect = Rect()
        mPaint.getTextBounds(mMaxLengthText, 0, mMaxLengthText.length, textRect)
        mTextWidth = textRect.right - textRect.left
        mItemHeight = max(mItemHeight, textRect.bottom - textRect.top)

        //计算数字的高度宽度
        mPaint.textSize = mValueSizePx
        mPaint.getTextBounds(mMaxValue.toString(), 0, mMaxValue.toString().length, textRect)
        mValueWidth = textRect.right - textRect.left
        mItemHeight = max(mItemHeight, textRect.bottom - textRect.top)

        mDrawableWidth =
            (measuredWidth - paddingStart - paddingEnd - mTextWidth - mValueWidth - mTextDrawableSpacePx - mValueDrawableSpacePx).toInt()


        mContentHeight =
            (mBarChartData.size * mItemHeight + (mBarChartData.size - 1) * mItemSpacePx).toInt()

        when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> {
                setMeasuredDimension(measuredWidth, mContentHeight + paddingTop + paddingBottom)
            }
            MeasureSpec.EXACTLY -> {
                if (mContentHeight < measuredHeight - paddingTop - paddingBottom) {
                    //重新计算itamSpace
                    mItemSpacePx =
                        (measuredHeight - paddingTop - paddingBottom - mItemHeight * mBarChartData.size).toFloat() / (mBarChartData.size - 1)
                }
            }
            MeasureSpec.AT_MOST -> {
                if (mContentHeight + paddingTop + paddingBottom > MeasureSpec.getSize(
                        heightMeasureSpec
                    )
                ) {
                    setMeasuredDimension(measuredWidth, MeasureSpec.getSize(heightMeasureSpec))
                    //重新计算itamSpace
                    mItemSpacePx =
                        (measuredHeight - paddingTop - paddingBottom - mItemHeight * mBarChartData.size).toFloat() / (mBarChartData.size - 1)
                } else {
                    setMeasuredDimension(measuredWidth, mContentHeight + paddingTop + paddingBottom)
                }
            }
        }
    }

    private fun getMode(measureSpec: Int): String {
        when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.UNSPECIFIED -> {
                return "MeasureSpec.UNSPECIFIED"
            }
            MeasureSpec.EXACTLY -> {
                return "MeasureSpec.EXACTLY"
            }
            MeasureSpec.AT_MOST -> {
                return "MeasureSpec.AT_MOST"
            }
            else -> {
                return ""
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        var currWidth: Float
        var currHeight: Float
        var fontMetrics: Paint.FontMetrics
        var distance = 0f
        var baseline = 0f
        for (index in mBarChartData.indices) {
            val data = mBarChartData[index]

            currWidth = paddingLeft.toFloat()
            currHeight = paddingTop + mItemHeight * index + mItemSpacePx * index
            //画文字
            mPaint.textSize = mTextSizePx
            mPaint.color = mTextColor!!
            val textRect = Rect()
            mPaint.getTextBounds(data.text, 0, data.text.length, textRect)
            fontMetrics = mPaint.fontMetrics
            distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
            baseline = currHeight + mItemHeight / 2 + distance
            canvas.drawText(
                data.text,
                0,
                data.text.length,
                (currWidth + mTextWidth - (textRect.right - textRect.left)).toFloat(),
                baseline,
                mPaint
            )
            currWidth += (mTextWidth + mTextDrawableSpacePx).toInt()

            //画柱图
            val drawbaleWidth = data.value.toFloat() / mMaxValue * mDrawableWidth
            mDrawable!!.setBounds(
                currWidth.toInt(),
                currHeight.toInt(),
                (currWidth + drawbaleWidth).toInt(),
                (currHeight + mItemHeight).toInt()
            )
            mDrawable!!.draw(canvas)

            currWidth += drawbaleWidth + mValueDrawableSpacePx.toInt()

            //画数字
            mPaint.textSize = mValueSizePx
            mPaint.color = mValueColor!!
            fontMetrics = mPaint.fontMetrics
            distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
            baseline = currHeight + mItemHeight / 2 + distance
            canvas.drawText(
                data.value.toString(),
                0,
                data.value.toString().length,
                currWidth,
                baseline,
                mPaint
            )
        }
        super.onDraw(canvas)
    }
}