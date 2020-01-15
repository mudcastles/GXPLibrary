package com.peng.gxplibrary

import com.peng.baseeverthing.widget.activity.GXPDrawerActivity

class DrawerNormalActivity : GXPDrawerActivity() {
    override fun getContentRootLayoutId(): Int =R.layout.layout_drawer_normal_content

    override fun getStartDrawerLayoutId(): Int? =R.layout.layout_drawer_normal_start

    override fun getEndDrawerLayoutId(): Int?=null

    override fun initView() {
        super.initView()
        setSwipeBackEnable(false)
    }
}
