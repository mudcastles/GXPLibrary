package com.peng.baseeverthing.backKey

//第一步：先定义一个FragmentBackHandler 接口。用于处理fragment的back键点击事件
interface FragmentBackHandler {
    fun onBackPressed(): Boolean
}