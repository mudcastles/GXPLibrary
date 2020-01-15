package com.peng.customwidget.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.TextView
import com.peng.baseeverthing.util.ChineseUtil
import java.lang.IllegalArgumentException
import kotlin.math.max


class VerticalTextView : TextView {
    private var charList: MutableList<CharInfo> = mutableListOf()

    val ENGLISH_DIRETION_LEFT = 0
    val ENGLISH_DIRETION_RIGHT = 1

    var englishDirection = ENGLISH_DIRETION_RIGHT
        set(value) {
            if (value == ENGLISH_DIRETION_LEFT || value == ENGLISH_DIRETION_RIGHT) {
                field = value
                invalidate()
            } else {
                throw IllegalArgumentException("englishDirection非法")
            }
        }
    private var currRotate = 0

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr, 0
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)


    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        charList.clear()
        val rect = Rect()
        var isChinese: Boolean
        var charWidth: Int = 0
        var charHeight: Int = 0
        text.toList().forEach {
            paint.getTextBounds(it.toString(), 0, 1, rect)
            isChinese = ChineseUtil.isChineseByScript(it)
            charWidth = if (isChinese) rect.right - rect.left else rect.bottom - rect.top
            charHeight = if (isChinese) rect.bottom - rect.top else rect.right - rect.left
            charList.add(CharInfo(it, isChinese, charWidth.toFloat(), charHeight.toFloat()))
        }

        var width = 0f
        when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> {
                width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
            }
            MeasureSpec.UNSPECIFIED -> {
                width = paddingStart + paddingEnd + getCharListMaxWidth(charList)
            }
            MeasureSpec.AT_MOST -> {
                width = paddingStart + paddingEnd + getCharListMaxWidth(charList)
                val parentMaxWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
                if (width > parentMaxWidth) width = parentMaxWidth
            }
        }

        var height = 0f
        when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> {
                height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
            }
            MeasureSpec.UNSPECIFIED -> {
                height = paddingTop + paddingBottom + getCharListHeight(charList)
            }
            MeasureSpec.AT_MOST -> {
                height = paddingTop + paddingBottom + getCharListHeight(charList)
                val parentMaxHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()
                if (height > parentMaxHeight) height = parentMaxHeight
            }
        }

        setMeasuredDimension(width.toInt(), height.toInt())
    }

    /*

    val fontMetrics = mPaint.fontMetrics
    val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
    var baseline = (location.bottom - location.top) / 4 + distance
    baseline = (location.bottom + roundRectTop) / 2 + distance
     */
    override fun onDraw(canvas: Canvas) {
        var y = paddingTop.toFloat()
        val fontMetrics = paint.fontMetrics
        var distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        charList.forEach {
            if (it.isChinese) {
                canvas.drawText(
                    it.char.toString(),
                    0,
                    1,
                    paddingStart.toFloat(),
                    y + it.height / 2 + distance,
                    paint
                )
                y += it.height + letterSpacing
            } else {
                canvas.save()
                distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
                if (englishDirection == ENGLISH_DIRETION_LEFT) {
                    canvas.rotate(90f)
                    canvas.translate(y, 0f)
                    canvas.drawText(
                        it.char.toString(),
                        0,
                        1,
                        0f,
                        -paddingStart - fontMetrics.descent,
                        paint
                    )
                } else {
                    canvas.rotate(-90f)
                    canvas.translate(-y - it.height, 0f)
                    canvas.drawText(
                        it.char.toString(),
                        0,
                        1,
                        0f,
                        measuredWidth - paddingEnd - fontMetrics.descent,
                        paint
                    )
                }

                canvas.restore()
                y += it.height + letterSpacing
            }
        }
    }

    /**
     *
     * @param charList 需要计算的char list
     * @param flag 计算宽还是高   0：width  1：height
     */
    private fun getCharListMaxWidth(charList: List<CharInfo>): Float {
        var size = 0f
        var charInfo: CharInfo
        for (i in charList.indices) {
            charInfo = charList[i]
            size = max(size, charInfo.width)
        }
        return size
    }

    private fun getCharListHeight(charList: List<CharInfo>): Float {
        var size = 0f
        var charInfo: CharInfo
        for (i in charList.indices) {
            charInfo = charList[i]
            size += charInfo.height + letterSpacing
        }
        size -= letterSpacing
        return size
    }


    private inner class CharInfo(
        val char: Char,
        val isChinese: Boolean,
        val width: Float,
        val height: Float
    )
}