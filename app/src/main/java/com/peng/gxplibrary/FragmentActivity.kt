package com.peng.gxplibrary

import com.peng.baseeverthing.widget.activity.GXPBaseActivity

class FragmentActivity : GXPBaseActivity() {

    override fun getRootLayoutId(): Int = R.layout.activity_fragment

    override fun initView() {
        super.initView()
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayout, BottomEdittextFragment.newInstance()).commitAllowingStateLoss()
    }
}
