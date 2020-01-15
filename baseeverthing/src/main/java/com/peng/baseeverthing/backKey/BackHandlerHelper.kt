package com.peng.baseeverthing.backKey

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager


//第二步：定义一个BackHandlerHelper工具类，用于实现分发back事件,Fragment和Activity的外理逻辑是一样，所以两者都需要调用该类的方法。
//第三步：在activity中复写onKeyDown方法，
    /*
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (BackHandlerHelper.handleBackPress(this)) {
            //执行到这里说明fragment拦截并处理了back事件
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
     */
//第四步，在fragment中重写onBackPressed()方法，如果fragment拦截back事件，则返回true
object BackHandlerHelper {
    /**
     * 将back事件分发给 FragmentManager 中管理的子Fragment，如果该 FragmentManager 中的所有Fragment都
     * 没有处理back事件，则尝试 FragmentManager.popBackStack()
     *
     * @return 如果处理了back键则返回 **true**
     * @see .handleBackPress
     * @see .handleBackPress
     */
    fun handleBackPress(fragmentManager: FragmentManager): Boolean {
        val fragments = fragmentManager.fragments

        for (i in fragments.indices.reversed()) {
            val child = fragments.get(i)

            if (isFragmentBackHandled(child)) {
                return true
            }
        }

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack()
            return true
        }
        return false
    }

    fun handleBackPress(fragment: Fragment): Boolean {
        return handleBackPress(fragment.getChildFragmentManager())
    }

    fun handleBackPress(fragmentActivity: FragmentActivity): Boolean {
        return handleBackPress(fragmentActivity.supportFragmentManager)
    }

    /**
     * 判断Fragment是否处理了Back键
     *
     * @return 如果处理了back键则返回 **true**
     */
    fun isFragmentBackHandled(fragment: Fragment?): Boolean {
        return (fragment != null
                && fragment.isVisible()
                && fragment.getUserVisibleHint() //for ViewPager

                && fragment is FragmentBackHandler
                && (fragment as FragmentBackHandler).onBackPressed())
    }
}