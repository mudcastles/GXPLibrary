package com.peng.gxplibrary

import android.graphics.drawable.Animatable
import android.widget.ImageView
import com.peng.baseeverthing.widget.activity.GXPToolbarDrawerActivity
import kotlinx.android.synthetic.main.layout_drawer_normal_content.*


class DrawerToolbarActivity : GXPToolbarDrawerActivity() {
    override fun getContentLayoutId(): Int = R.layout.layout_drawer_normal_content

    override fun getStartDrawerLayoutId(): Int? = R.layout.layout_drawer_normal_start

    override fun getEndDrawerLayoutId(): Int? = null
    private var isPlayChecked = true
    override fun initView() {
        super.initView()
        actionBar().title = "Toolbar标题"
        actionBar().setBackgroundResource(R.drawable.shape_main)

        ivImage.setOnClickListener {
            val stateSet =
                intArrayOf(android.R.attr.state_checked * if (isPlayChecked) 1 else -1)
            (it as ImageView).setImageState(stateSet, true)
            isPlayChecked = !isPlayChecked

//            val animatable = (it as ImageView).drawable as Animatable
//            if (animatable.isRunning) animatable.stop()
//            else animatable.start()
        }
    }

    override fun initImmersionBar() {
        super.initImmersionBar()
    }
}