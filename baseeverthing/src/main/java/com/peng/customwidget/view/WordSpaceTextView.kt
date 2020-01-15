package com.example.brilonlinestudy.widget

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ScaleXSpan
import android.util.AttributeSet
import android.widget.TextView


class WordSpaceTextView : TextView {
    /**
     * 获取字间距
     */
    /**
     * 设置间距
     */
    var spacing = Spacing.NORMAL
        set(spacing) {
            field = spacing
            applySpacing()
        }
    private var originalText: CharSequence? = ""

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun setText(text: CharSequence, type: TextView.BufferType) {
        originalText = text
        applySpacing()
    }

    override fun getText(): CharSequence? {
        return originalText
    }

    /**
     * 扩大文字空间
     */
    private fun applySpacing() {
        if (this == null || this.originalText == null) return
        val builder = StringBuilder()
        for (i in 0 until originalText!!.length) {
            builder.append(originalText!![i])
            if (i + 1 < originalText!!.length) {
                //如果前后都是英文，则不添加空格，防止英文空格太大
                if (isEnglish(originalText!![i] + "") && isEnglish(originalText!![i + 1] + "")) {
                } else {
                    // \u00A0 不间断空格 碰见文字追加空格
                    builder.append("\u00A0")
                }
            }
        }
        // 通过SpannableString类，去设置空格
        val finalText = SpannableString(builder.toString())
        // 如果当前TextView内容长度大于1，则进行空格添加
        if (builder.toString().length > 1) {
            var i = 1
            while (i < builder.toString().length) {
                // ScaleXSpan 基于x轴缩放  按照x轴等比例进行缩放 通过字间距+1除以10进行等比缩放
                finalText.setSpan(ScaleXSpan((this.spacing + 1) / 10), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                i += 2
            }
        }
        super.setText(finalText, TextView.BufferType.SPANNABLE)
    }

    object Spacing {
        val NORMAL = 0f
    }

    companion object {
        /**
         * 判断是否是英语
         */
        fun isEnglish(charaString: String): Boolean {
            return charaString.matches("^[a-zA-Z]*".toRegex())
        }
    }

}
