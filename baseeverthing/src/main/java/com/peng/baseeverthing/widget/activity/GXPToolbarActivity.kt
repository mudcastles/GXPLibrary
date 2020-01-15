package com.peng.baseeverthing.widget.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.annotation.StyleRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import com.gyf.immersionbar.ImmersionBar
import com.peng.baseeverthing.R
import kotlinx.android.synthetic.main.gxp_activity_base.*
import kotlinx.android.synthetic.main.gxp_layout_toolbar.*


abstract class GXPToolbarActivity : GXPBaseActivity() {
    final override fun getRootLayoutId(): Int = R.layout.gxp_activity_base

    /** -------------------生命周期----------------------*/
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        //如果内容不延伸到actionBar以及statusBar，则应设置marginTop
        if (!isContentExtendToStatusBar()) {
            val contentView = com_peng_baseeverthing_root_view.getChildAt(0)
            val params = contentView.layoutParams as FrameLayout.LayoutParams
            params.topMargin = ImmersionBar.getStatusBarHeight(this) + ImmersionBar.getActionBarHeight(this)
            contentView.layoutParams = params
        }
    }

    /** -------------------按需求实现相关初始化方法----------------------*/
    /*内容部分是否延伸到statusBar，true，不需要做任何操作，false，需要为根布局设置padding或marginTop*/
    open fun isContentExtendToStatusBar() = false
    override fun initView() {
        super.initView()
        com_peng_baseeverthing_viewStub.apply {
            layoutResource = getContentLayoutId()
            inflate()
        }
    }
    /**获取toolbar，来设置title、subtitle、appearance等*/
   final fun actionBar() = com_peng_baseeverthing_toolbar

    override fun initImmersionBar() {
        super.initImmersionBar()
        ImmersionBar.with(this).titleBar(R.id.com_peng_baseeverthing_toolbar).init()
    }
    /**
     * 子类设置布局Id
     *
     * @return the layout id
     */
    protected abstract fun getContentLayoutId(): Int

    fun enableLeftIcon() {
        enableLeftIcon(R.drawable.ic_back, object : OnToolbarIconClickListener {
            override fun onLeftIconClicked() {
                if (isBaseActivity()) {
                    doubleClickExit()
                } else {
                    finish()
                }
            }
        })
    }

    fun enableLeftIcon(@DrawableRes drawableRes: Int, function: OnToolbarIconClickListener) {
        com_peng_baseeverthing_toolbar.setNavigationIcon(drawableRes)
        com_peng_baseeverthing_toolbar.setNavigationOnClickListener {
            function.onLeftIconClicked()
        }
    }

    private var menuRes: Int? = null
    fun addMenuIcons(@DrawableRes overflowIcon: Int?, @MenuRes menuRes: Int, menuItemClickListener: Toolbar.OnMenuItemClickListener) {
        this.menuRes = menuRes
        //设置溢出图片  如果不设置会默认使用系统灰色的图标
        if (overflowIcon != null)
            com_peng_baseeverthing_toolbar.overflowIcon = ResourcesCompat.getDrawable(resources, overflowIcon, theme)
        //填充menu
        com_peng_baseeverthing_toolbar.inflateMenu(menuRes)
        //设置点击事件
        com_peng_baseeverthing_toolbar.setOnMenuItemClickListener(menuItemClickListener)
    }

    /** 创建OptionsMenu */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return if (menuRes != null) {
            menuInflater.inflate(menuRes!!, menu)
            true
        } else super.onCreateOptionsMenu(menu)
    }

    /** 监听OptionsMenu的点击事件 */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    fun setToolbarPopupTheme(@StyleRes popupTheme: Int) {
        com_peng_baseeverthing_toolbar.popupTheme = popupTheme
    }

    /** -------------------接口----------------------*/
    interface OnToolbarIconClickListener {
        fun onLeftIconClicked()
    }
}