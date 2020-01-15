package com.peng.baseeverthing.widget.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.components.SimpleImmersionFragment
import com.peng.baseeverthing.backKey.BackHandlerHelper
import com.peng.baseeverthing.backKey.FragmentBackHandler


/**
 * Fragment预加载问题的解决方案：
 * 1.可以懒加载的Fragment
 * 2.切换到其他页面时停止加载数据（可选）
 *
 * 支持沉浸
 */

abstract class BaseLazyLoadFragment : SimpleImmersionFragment(), FragmentBackHandler {
    private val TAG = "BaseLazyLoadFragment"

    /**
     * 视图是否已经初初始化
     */
    private var isInit = false
    private var isLoad = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isInit = true
        return inflater.inflate(getLayout(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e(TAG, "onViewCreated   ${javaClass.simpleName}   ${this.hashCode()}")
        super.onViewCreated(view, savedInstanceState)
        fitsLayoutOverlap()
        initView()
        isCanLoadData()
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //旋转屏幕为什么要重新设置布局与状态栏重叠呢？因为旋转屏幕有可能使状态栏高度不一样，如果你是使用的静态方法修复的，所以要重新调用修复
        fitsLayoutOverlap()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isCanLoadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isInit = false
        isLoad = false
    }


    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
    private fun isCanLoadData() {
        if (!isInit) {
            return
        }

        if (getUserVisibleHint()) {
            resumeLoad()
            isLoad = true
        } else {
            if (isLoad) {
                stopLoad()
            }
        }
    }


    private var toast: Toast? = null
    fun showToast(message: String?) {
        if (toast == null)
            toast = Toast.makeText(this.requireActivity().applicationContext, "", Toast.LENGTH_SHORT)
        toast!!.setText(message)
        toast!!.show()
    }


    //return false表示不处理back点击事件
    override fun onBackPressed(): Boolean {
        return BackHandlerHelper.handleBackPress(this)
    }

    private  fun fitsLayoutOverlap() {
        if (getStatusBarView() != null) {
            ImmersionBar.setStatusBarView(this, getStatusBarView())
        } else {
            ImmersionBar.setTitleBar(this, getTitleBarView())
        }
    }

    abstract fun getLayout(): Int
    /**
     * 第一次初始化的时候会调用
     */
    abstract fun initView()

    /**
     * 开始加载数据，当fragment可见时调用
     */
    open fun resumeLoad(){

    }
    /**
     * 当页面销毁时调用，按需要复写
     */
    open fun stopLoad(){
    }

    /**
     * 获取状态栏View，开发者自己实现
     */
    protected open fun getStatusBarView():View? = null
    /**
     * 获取标题栏View，开发者自己实现
     */
    protected open fun getTitleBarView():View? = null

    override fun initImmersionBar() {
        ImmersionBar.with(this).keyboardEnable(true).init()
    }
}