package com.peng.baseeverthing.util

import android.content.Context

object ScreenUtil {
    //获取状态栏高度
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        var resourceId = context.getResources().getIdentifier(
            "status_bar_height", "dimen", "android"
        )
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 获取导航栏高度
     * @param context
     * @return
     */
    fun getDaoHangHeight(context: Context): Int {
        var result = 0
        var resourceId = 0
        var rid = context.resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return if (rid != 0) {
            resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android");
            context.resources.getDimensionPixelSize(resourceId)
        } else
            0
    }


    fun getScreenWidth(context: Context): Int = context.resources.displayMetrics.widthPixels

    fun getScreenHeight(context: Context): Int = context.resources.displayMetrics.heightPixels
}