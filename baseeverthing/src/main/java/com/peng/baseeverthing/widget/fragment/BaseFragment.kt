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
 * 不支持懒加载的Fragment基类
 * 支持沉浸
 */
//没有处理back键需求的Fragment不用实现onBackPressed方法
abstract class BaseFragment : SimpleImmersionFragment(), FragmentBackHandler {
    private var TAG = "BaseFragment"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayout(), container, false)
    }

    abstract fun getLayout(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e(TAG, "onViewCreated   ${javaClass.simpleName}   ${this.hashCode()}")
        super.onViewCreated(view, savedInstanceState)
        fitsLayoutOverlap()
        initData()
        initView()
        setListener()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //旋转屏幕为什么要重新设置布局与状态栏重叠呢？因为旋转屏幕有可能使状态栏高度不一样，如果你是使用的静态方法修复的，所以要重新调用修复
        fitsLayoutOverlap()
    }

    private var toast: Toast? = null
    fun showToast(message: String?) {
        if (toast == null)
            toast = Toast.makeText(this.requireActivity().applicationContext, "", Toast.LENGTH_SHORT)
        toast!!.setText(message)
        toast!!.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
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

    /**
     * 初始化数据
     */
    protected open fun initData() {}

    /**
     * view与数据绑定
     */
    protected open fun initView() {}

    /**
     * 设置监听
     */
    protected open fun setListener() {}


    /**
     * 获取状态栏View
     */
    protected open fun getStatusBarView():View? = null
    /**
     * 获取标题栏View
     */
    protected open fun getTitleBarView():View? = null

    override fun initImmersionBar() {
        ImmersionBar.with(this).keyboardEnable(true).init()
    }
}