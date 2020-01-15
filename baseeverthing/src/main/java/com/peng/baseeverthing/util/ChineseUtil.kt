package com.peng.baseeverthing.util

import android.annotation.TargetApi
import android.os.Build

object ChineseUtil {
    /**
     * 判断字符是否是中文
     */
    fun isChineseByScript(c: Char): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //使用UnicodeScript方法判断
            val sc = Character.UnicodeScript.of(c.toInt())
            return sc == Character.UnicodeScript.HAN
        } else {
            //使用UnicodeBlock方法判断
            val ub = Character.UnicodeBlock.of(c)
            return ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
        }
    }

    /**
     * 根据UnicodeBlock方法判断中文标点符号
     */
    fun isChinesePunctuation(c: Char): Boolean {
        val ub = Character.UnicodeBlock.of(c)
        return ub === Character.UnicodeBlock.GENERAL_PUNCTUATION || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS || ub === Character.UnicodeBlock.VERTICAL_FORMS
    }
}