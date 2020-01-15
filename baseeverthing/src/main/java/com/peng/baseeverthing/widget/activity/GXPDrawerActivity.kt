package com.peng.baseeverthing.widget.activity

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.peng.baseeverthing.R
import kotlinx.android.synthetic.main.gxp_activity_drawer.*

abstract class GXPDrawerActivity : GXPBaseActivity() {

    final override fun getRootLayoutId(): Int = R.layout.gxp_activity_drawer

    override fun initView() {
        super.initView()
        setSwipeBackEnable(false)

        com_peng_baseeverthing_viewStubContent.apply {
            layoutResource = getContentRootLayoutId()
            inflate()
        }

        if (getStartDrawerLayoutId() == null && getEndDrawerLayoutId() == null) {
            throw IllegalArgumentException("请指定左侧或右侧侧滑菜单!!")
        }

        com_peng_baseeverthing_viewStubStartDrawer.apply {
            layoutResource =
                if (getStartDrawerLayoutId() != null) getStartDrawerLayoutId()!! else R.layout.layout_nothing
            inflate()
        }
        com_peng_baseeverthing_viewStubEndDrawer.apply {
            layoutResource =
                if (getEndDrawerLayoutId() != null) getEndDrawerLayoutId()!! else R.layout.layout_nothing
            inflate()
        }
        if (getStartDrawerLayoutId() == null)
            com_peng_baseeverthing_drawerLayout.setDrawerLockMode(
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                GravityCompat.START
            )
        if (getEndDrawerLayoutId() == null)
            com_peng_baseeverthing_drawerLayout.setDrawerLockMode(
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                GravityCompat.END
            )

    }


    /** -------------------按需求实现相关初始化方法----------------------*/
    protected abstract fun getContentRootLayoutId(): Int

    protected abstract fun getStartDrawerLayoutId(): Int?
    protected abstract fun getEndDrawerLayoutId(): Int?
}