package com.peng.customwidget.view

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.peng.baseeverthing.R
import com.peng.baseeverthing.util.DimenUtil
import com.peng.baseeverthing.util.ScreenUtil
import kotlinx.android.synthetic.main.layout_keyboard.view.*
import kotlin.math.max

/**
 * 键盘，包含了AnimatorButton，实现切换过程的动画
 */
class KeyBoardLayout : ViewGroup, AnimatorButton.OnExpandListener,
    AnimatorButton.OnButtonClickedListener, View.OnClickListener {
    private val mInflater = LayoutInflater.from(context)
    private var mKeyBoard: View = mInflater.inflate(R.layout.layout_keyboard, null)
    private var mButton: AnimatorButton = AnimatorButton(context)

    private var mWidthMeasureSpec: Int = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
    private var mHeightMeasureSpec: Int = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
    private var mExpandProgress = 0f //展开进度
    private var mButtonOriginX = 0   //button最初的x
    private var mButtonTranslateX = 0   //button最终要移动的距离
    private val mButtonOriginLocation = intArrayOf(0, 0)

    private var mTotalWidth = 0
    private var mTotalHeight = 0

    var mOnCallListener: OnCallListener? = null
    var mOnHandUpListener: OnHandupListener? = null
    private val teleohoneSb: StringBuffer = StringBuffer()

    private val mScrrenWidth = ScreenUtil.getScreenWidth(context)
    private val mScrrenHeight = ScreenUtil.getDaoHangHeight(context)
    //默认paddingBottom
    var mDefaultPaddingBottom = DimenUtil.dip2px(context, 20f)
    var mDefaultPaddingEnd = DimenUtil.dip2px(context, 20f)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        addView(mKeyBoard)
        mButton.setCircleWidthHeight(50f)
        mButton.setDrawablePadding(4f)
        mButton.setTextPaddingVertical(10f)
        mButton.setTextSize(14)
        mButton.addButtonItems(
            AnimatorButton.ButtonItem(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_handup,
                    context.theme
                )!!,
                "挂断",
                Color.RED
            ),
            AnimatorButton.ButtonItem(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_call_1,
                    context.theme
                )!!,
                "拨打",
                Color.GREEN
            )
        )
        addView(mButton)
        mButton.setOnExpandListener(this)
        mButton.setOnButtonClickedListener(this)
        initKeyBoardClicked()
        tvTelephone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) ivDelete.visibility = View.GONE
                else ivDelete.visibility = View.VISIBLE
            }
        })
    }

    private fun initKeyBoardClicked() {
        tvGLI0.setOnClickListener(this)
        tvGLI1.setOnClickListener(this)
        tvGLI2.setOnClickListener(this)
        tvGLI3.setOnClickListener(this)
        tvGLI4.setOnClickListener(this)
        tvGLI5.setOnClickListener(this)
        tvGLI6.setOnClickListener(this)
        tvGLI7.setOnClickListener(this)
        tvGLI8.setOnClickListener(this)
        tvGLI9.setOnClickListener(this)
        tvGLIStart.setOnClickListener(this)
        tvGLISharp.setOnClickListener(this)
        ivDelete.setOnClickListener(this)
        ivGLIFold.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvGLI0,
            R.id.tvGLI1,
            R.id.tvGLI2,
            R.id.tvGLI3,
            R.id.tvGLI4,
            R.id.tvGLI5,
            R.id.tvGLI6,
            R.id.tvGLI7,
            R.id.tvGLI8,
            R.id.tvGLI9,
            R.id.tvGLIStart,
            R.id.tvGLISharp -> {
                if (teleohoneSb.length >= 12) return
                teleohoneSb.append((v as TextView).text)
                tvTelephone.text = teleohoneSb.toString()
            }
            R.id.ivDelete -> {
                teleohoneSb.deleteCharAt(teleohoneSb.length - 1)
                tvTelephone.text = teleohoneSb.toString()
            }
            R.id.ivGLIFold -> mButton.toggle()
        }
    }

    fun toggle() = mButton.toggle()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mButton.getStatus() == AnimatorButton.AnimatorStatus.FOLDED) {
            mWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            measureChild(mButton, mWidthMeasureSpec, mHeightMeasureSpec)
            mTotalWidth = mButton.measuredWidth
            mTotalHeight = mButton.measuredHeight + mDefaultPaddingBottom

        } else {
            mWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            measureChild(mButton, mWidthMeasureSpec, mHeightMeasureSpec)
            mWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.EXACTLY
            )
            measureChild(mKeyBoard, mWidthMeasureSpec, mHeightMeasureSpec)
            mTotalWidth = MeasureSpec.getSize(widthMeasureSpec)
            mTotalHeight = max(
                (mButton.measuredHeight + mDefaultPaddingBottom).toFloat(),
                mKeyBoard.measuredHeight * mExpandProgress
            ).toInt()

        }
        setMeasuredDimension(mTotalWidth, mTotalHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mKeyBoard.layout(
            0,
            (measuredHeight - mKeyBoard.measuredHeight * mExpandProgress).toInt(),
            measuredWidth,
            measuredHeight
        )
        if (mButton.getStatus() == AnimatorButton.AnimatorStatus.FOLDED) {
            mButton.layout(0, 0, mButton.measuredWidth, mButton.measuredHeight)
            mButton.getLocationOnScreen(mButtonOriginLocation)
            mButtonOriginX = mButtonOriginLocation[0]
        } else {
            mButtonTranslateX =
                mButtonOriginX - (mTotalWidth - mButton.getExpandedWidth()) / 2 - left
            mButton.layout(
                (mButtonOriginX - mButtonTranslateX * mExpandProgress).toInt(),
                measuredHeight - mButton.measuredHeight - mDefaultPaddingBottom,
                (mButtonOriginX - mButtonTranslateX * mExpandProgress + mButton.measuredWidth).toInt(),
                measuredHeight - mDefaultPaddingBottom
            )
        }
    }

    override fun onButtonClicked(index: Int) {
        when (index) {
            0 -> {    //挂断电话
                mOnHandUpListener?.onHandUp()
            }
            1 -> {    //拨打电话
                mOnCallListener?.onCall(tvTelephone.text.toString())
            }
        }
    }

    override fun onExpand(progress: Float) {
        mExpandProgress = progress
        requestLayout()
    }

    fun getKeyBoardStatus() = mButton.getStatus()

}

interface OnCallListener {
    fun onCall(num: String)
}

interface OnHandupListener {
    fun onHandUp()
}