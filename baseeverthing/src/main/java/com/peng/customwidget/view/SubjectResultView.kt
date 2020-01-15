package com.peng.administrator.qhdstudyonline.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.peng.baseeverthing.util.DimenUtil
import com.peng.customwidget.model.Subject

class SubjectResultView : View {
    private var mOnItemClickListener: OnItemClickListener? = null

    private lateinit var mCirclePaint: Paint
    private lateinit var mTextPaint: Paint
    /**
     * 题目
     */
    private var mSubjects = mutableListOf<Subject>()

    /**
     * 每个圆圈的中心点
     */
    private var mCenterPoints = mutableListOf<PointF>()

    /**
     * 每行显示的题目数量，默认为5
     */
    private var mSubjectNumPeerRow = 5

    /**
     * 圆半径，初始化时将会默认为20dp
     */
    private var mCircleRadius = 0
    /**
     *第一个圆的X
     */
    private var mFirstCircleX = 0
    /**
     *第一个圆的Y
     */
    private var mFirstCircleY = 0
    /**
     * 横向两个圆的圆心之间的空隙,mGapX = (View总宽度 - mCircleRadius * 2 - paddingStart - paddingEnd)/(mSubjectNumPeerRow - 1)
     */
    private var mGapX = 0
    /**
     * 纵向两个圆的圆心之间的空隙,mGapY = (View的总高度 - mCircleRadius * 2 - paddingTop - paddingBottom)/ (Math.ceil(mSubjectNum.toDouble() / mSubjectNumPeerRow).toInt() - 1)
     * 默认是50dp
     */
    private var mGapY = 0

    /**
     * 文字大小
     */
    private var mTextSize: Float = 0.0f

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mCircleRadius = DimenUtil.dip2px(context, 15f)
        mTextSize = DimenUtil.sp2px(context, 16f).toFloat()

        mCirclePaint = Paint()
        mCirclePaint.style = Paint.Style.FILL
        mCirclePaint.isAntiAlias = true

        mTextPaint = TextPaint()
        mTextPaint.isAntiAlias = true
        mTextPaint.color = Color.WHITE
        mTextPaint.textSize = mTextSize

    }

    //将View的高度设置为wrap
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        calValues()
        setMeasuredDimension(
            measuredWidth,
            paddingTop + paddingBottom + 2 * mCircleRadius + mGapY * (Math.ceil(mSubjects.size.toDouble() / mSubjectNumPeerRow).toInt() - 1)
        )
    }

    /**
     * 计算需要的值
     */
    private fun calValues() {
        mFirstCircleX = paddingStart + mCircleRadius
        mFirstCircleY = paddingTop + mCircleRadius
        mGapX = (measuredWidth - mCircleRadius * 2 - paddingStart - paddingEnd) / (mSubjectNumPeerRow - 1)
        mGapY = DimenUtil.dip2px(context, 50f)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        mCenterPoints.clear()
        for (index in mSubjects.indices) {
            val subject = mSubjects[index]
            if (subject.isRight == 1) {
                //错误
                mCirclePaint.color = Color.parseColor("#FF5A5A")
            } else if (subject.isRight == 3) {
                mCirclePaint.color = Color.parseColor("#2081FF")
            }
            val col = index.rem(mSubjectNumPeerRow) //计算第几列，从0开始
            val row = index.div(mSubjectNumPeerRow)    //计算第几行，从0开始

            val circleCenterX = (mFirstCircleX + col * mGapX).toFloat()
            val circleCenterY = (mFirstCircleY + row * mGapY).toFloat()

            canvas.drawCircle(circleCenterX, circleCenterY, mCircleRadius.toFloat(), mCirclePaint)

            val text = "${index + 1}"
            val mTextRect = Rect()
            mTextPaint.getTextBounds(text, 0, text.length, mTextRect)
            canvas.drawText(
                text,
                circleCenterX - (mTextRect.right - mTextRect.left) / 2,
                circleCenterY + (mTextRect.bottom - mTextRect.top) / 2,
                mTextPaint
            )

            mCenterPoints.add(PointF(circleCenterX, circleCenterY))
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        for (index in mCenterPoints.indices) {
            val point = mCenterPoints[index]
            if (x > point.x - mCircleRadius && x < point.x + mCircleRadius && y > point.y - mCircleRadius && y < point.y + mCircleRadius) {
                mOnItemClickListener?.onItemClicked(mSubjects[index], index)
            }
        }

        return super.onTouchEvent(event)
    }

    fun setSubjects(subjects: MutableList<Subject>) {
        this.mSubjects = subjects

    }

    fun getSubjects(): MutableList<Subject> = mSubjects

    fun setTextSize(textSize: Float) {
        setTextSize(textSize, false)
    }

    fun setTextSize(textSize: Float, sp: Boolean) {
        if (sp)
            this.mTextSize = DimenUtil.sp2px(context, textSize).toFloat()
        else
            this.mTextSize = textSize
        mTextPaint.textSize = this.mTextSize
    }

    fun getTextSize(): Float = mTextSize


    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClicked(subject: Subject, position: Int)
    }
}