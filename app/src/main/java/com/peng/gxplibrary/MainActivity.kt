package com.peng.gxplibrary

import android.content.Intent
import android.graphics.LinearGradient
import com.peng.baseeverthing.widget.activity.GXPToolbarActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : GXPToolbarActivity() {
    override fun getContentLayoutId(): Int = R.layout.activity_main
    override fun isBaseActivity(): Boolean = true
    //    override fun isContentExtendToStatusBar(): Boolean =true
    override fun initView() {
        super.initView()
        actionBar().title = "沉浸式测试"
        actionBar().isTitleCenter = true
        actionBar().setBackgroundResource(R.drawable.shape_main)
        enableLeftIcon()
        startActivity(Intent(this, DrawerToolbarActivity::class.java))
    }
}
