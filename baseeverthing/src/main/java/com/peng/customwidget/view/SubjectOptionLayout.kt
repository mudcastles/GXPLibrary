package com.peng.customwidget.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.peng.baseeverthing.R
import com.peng.baseeverthing.util.DimenUtil
import java.io.Serializable

/**
 * 题目选项View
 */
class SubjectOptionLayout : ViewGroup {


    /**
     * 题目选项
     */
    private var mOptions = mutableListOf<String>()

    /**
     * 所有题目的对题状态,在答题模式中，OptionStatus.OPTION_STATUS_WRONG不会生效
     */
    private var mOptionStatus = mutableListOf<OptionStatus>()

    /**
     * 当前显示模式，默认为答题模式
     */
    private var mCurrShowMode: ShowMode =
        ShowMode.SHOW_MODE_ANSWER

    fun setCurrShowMode(showMode: ShowMode) {
        this.mCurrShowMode = showMode
    }

    fun getCurrShowMode(): ShowMode = mCurrShowMode

    /**
     * checkbox的点击事件
     */
    private var mOnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        when (mCurrShowMode) {
            ShowMode.SHOW_MODE_ANSWER -> {  //答题状态
                if (isChecked) { //由未选中变为选中
                    //选择限制
                    if (mMaxSelectNum == 1) {    //针对单选题特殊处理，以提高用户体验
                        for (index in 0 until childCount) {
                            updateCheckBoxDrawable(getChildAt(index) as CheckBox,
                                OptionStatus.OPTION_STATUS_NONE
                            )
                        }
                        updateCheckBoxDrawable(buttonView,
                            OptionStatus.OPTION_STATUS_RIGHT
                        )
                        return@OnCheckedChangeListener
                    }

                    if (getSelectedOptionIndexList(OptionStatus.OPTION_STATUS_RIGHT).size >= mMaxSelectNum) {
                        return@OnCheckedChangeListener
                    }
                    updateCheckBoxDrawable(buttonView,
                        OptionStatus.OPTION_STATUS_RIGHT
                    )
                } else {  //由选中变为未选中
                    updateCheckBoxDrawable(buttonView,
                        OptionStatus.OPTION_STATUS_NONE
                    )
                }
            }
            else -> {   //差错状态时，checkbox必须不可以改变状态
            }
        }
    }


    /**
     * 选项之间的margin
     */
    private var mOpionMargin = 0

    /**
     * 最多可以选择mMaxSelectNum项，默认为单选
     */
    private var mMaxSelectNum = 1

    /**
     * 文本大小
     */
    private var mTextSize: Float = 12f

    /**
     * drawableStart的尺寸，必须重新设置 否则不会显示
     */
    private var mDrawableSize = 0


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initValue(attrs)
    }

    private fun initValue(attrs: AttributeSet?) {
        val ta = context?.obtainStyledAttributes(attrs, R.styleable.SubjectOptionLayout)
        if (ta != null) {
            mDrawableSize =
                ta.getDimension(R.styleable.SubjectOptionLayout_drawableSize, DimenUtil.dip2px(context, 14f).toFloat())
                    .toInt()
            mMaxSelectNum = ta.getInt(R.styleable.SubjectOptionLayout_maxSelectNum, 1)
            mOpionMargin = ta.getDimension(R.styleable.SubjectOptionLayout_optionMargin, 0f).toInt()
            mTextSize =
                ta.getDimension(R.styleable.SubjectOptionLayout_textSize, 14f)

            ta.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val mMeasureWidthSpec =
            View.MeasureSpec.makeMeasureSpec(measuredWidth - paddingStart - paddingEnd, MeasureSpec.AT_MOST)

        var totalHeight = paddingTop + paddingBottom
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            child.measure(mMeasureWidthSpec, heightMeasureSpec)
            totalHeight += child.measuredHeight
            if (index < childCount - 1) totalHeight += mOpionMargin
        }

        setMeasuredDimension(measuredWidth, totalHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currHeight = paddingTop
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            child.layout(
                paddingStart,
                currHeight,
                paddingStart + child.measuredWidth,
                currHeight + child.measuredHeight
            )
            currHeight += child.measuredHeight + mOpionMargin
        }
    }

    /**
     * 设置多选选项个数
     */
    fun setMaxSelectNum(mMaxSelectNum: Int) {
        this.mMaxSelectNum = mMaxSelectNum
    }

    /**
     * 设置选项
     */
    fun setOptions(options: List<String>) {
        setOptions(options,
            OptionStatus.OPTION_STATUS_NONE
        )
    }

    /**
     * 设置选项
     */
    fun setOptions(options: List<String>, optionStatus: OptionStatus) {
        val statusList = mutableListOf<OptionStatus>()
        for (index in options.indices) {
            statusList.add(optionStatus)
        }
        setOptions(options, statusList)
    }

    /**
     * 设置选项
     */
    fun setOptions(options: List<String>, optionStatus: List<OptionStatus>) {
        if (options.size != optionStatus.size) {
            throw Exception("对题状态与选项数量不一致")
        }
        removeAllOptions()
        for (index in options.indices) {
            addOption(options[index], optionStatus[index])
        }
    }

    /**
     * 重置状态并删除所有选项
     */
    fun removeAllOptions() {
        mOptions.clear()
        mOptionStatus.clear()
        removeAllViews()
    }


    /**
     * 添加子View
     */
    fun addOption(itemContent: String) {
        addOption(itemContent,
            OptionStatus.OPTION_STATUS_NONE
        )
    }

    /**
     * 添加子View,并设置对应对题状态
     */
    fun addOption(itemContent: String, optionStatus: OptionStatus) {
        mOptions.add(itemContent)
        //将选项标记为无状态
        mOptionStatus.add(OptionStatus.OPTION_STATUS_NONE)
        val mItemView = LayoutInflater.from(context).inflate(R.layout.item_subject_option, this, false)
        if (mItemView is CheckBox) {
            val param = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            mItemView.layoutParams = param
            mItemView.text = itemContent
            mItemView.textSize = DimenUtil.px2sp(context, mTextSize).toFloat()

            mItemView.setTag(R.id.OPTION_INDEX, mOptions.size - 1)

            //设置drawableStart
            updateCheckBoxDrawable(mItemView, optionStatus)

            mItemView.setOnCheckedChangeListener(mOnCheckedChangeListener)
        }
        addView(mItemView)
    }

    /**
     * 更新checkbox的drawableStart
     * @param buttonView 要更新的checkbox
     * @param optionStatus 更新的目的状态
     */
    private fun updateCheckBoxDrawable(
        buttonView: CompoundButton,
        optionStatus: OptionStatus
    ) {
        //修改状态，首先要将该buttonView对应的mOptionStatus的值改变
        mOptionStatus[buttonView.getTag(R.id.OPTION_INDEX) as Int] = optionStatus

        //然后修改drawableStart
        var drawable: Drawable? = null
        when (optionStatus) {
            OptionStatus.OPTION_STATUS_NONE -> {
                buttonView.isChecked = false
                drawable = DrawableCompat.wrap(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.button_option_normal,
                        context.theme
                    )!!
                ).mutate()
            }
            OptionStatus.OPTION_STATUS_RIGHT -> {
                buttonView.isChecked = true
                drawable = DrawableCompat.wrap(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.button_option_right,
                        context.theme
                    )!!
                ).mutate()
            }
            OptionStatus.OPTION_STATUS_WRONG -> {
                buttonView.isChecked = true
                drawable = DrawableCompat.wrap(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.button_option_wrong,
                        context.theme
                    )!!
                ).mutate()
            }
        }
        drawable!!.setBounds(0, 0, mDrawableSize, mDrawableSize)
        buttonView.setCompoundDrawables(drawable, null, null, null)
    }

    /**
     * 获取所有optionStatus状态的选项下标
     */
    fun getSelectedOptionIndexList(optionStatus: OptionStatus): List<Int> {
        val indexList = mutableListOf<Int>()
        for (index in mOptionStatus.indices) {
            if (mOptionStatus[index] == optionStatus) indexList.add(index)
        }
        return indexList
    }

    /**
     * 获取所有optionStatus状态的选项内容，包含选项和序号
     */
    fun getSelectedOptions(optionStatus: OptionStatus): List<String> {
        val options = mutableListOf<String>()
        for (index in mOptionStatus.indices) {
            if (mOptionStatus[index] == optionStatus) options.add(mOptions[index])
        }
        return options
    }

    /**
     * 获取所有optionStatus状态的选项内容,只包含内容，不包含选项序号
     */
    fun getSelectedOptionsOnlyContent(optionStatus: OptionStatus): List<String> {
        val options = mutableListOf<String>()
        for (index in mOptionStatus.indices) {
            if (mOptionStatus[index] == optionStatus) options.add(mOptions[index].substring(mOptions[index].indexOfFirst {
                it == '.'
            } + 1))
        }
        return options
    }

    /**
     * 设置选中的选项
     * @param optionIndexes 被选中选项的下标
     */
    fun selectOptions(optionIndexes: List<Int>) {
        optionIndexes.forEach {
            updateCheckBoxDrawable(getChildAt(it) as CheckBox,
                OptionStatus.OPTION_STATUS_RIGHT
            )
        }
    }


    /**
     * 题目状态，无状态，对题，错题
     */
    enum class OptionStatus : Serializable {
        OPTION_STATUS_NONE, OPTION_STATUS_RIGHT, OPTION_STATUS_WRONG
    }

    /**
     * 可视模式：答题模式、查错模式
     *
     * 答题模式下，checkbox选中和未选中状态分别只有一种drawableStart，
     *      选中时只显示“对题”(SUBJECT_STATUS_RIGHT)，
     *      未选中时为“无状态”(SUBJECT_STATUS_NONE)
     * 差错模式下，checkbox选中和未选中状态分别有2、1中状态，
     *      选中时分为“对题”(SUBJECT_STATUS_RIGHT)和“错题”(SUBJECT_STATUS_WRONG)，
     *      未选中时为“无状态”(SUBJECT_STATUS_NONE)
     */
    enum class ShowMode : Serializable {
        SHOW_MODE_ANSWER, SHOW_MODE_CHECK_ERROR
    }
}